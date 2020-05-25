package com.purrfact

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.purrfact.services.CatFactService
import com.purrfact.services.Fact
import com.purrfact.swipeCards.CardStackAdapter
import com.purrfact.swipeCards.Spot
import com.purrfact.swipeCards.SpotDiffCallback
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.*

class CardsActivity: AppCompatActivity(), CardStackListener {

    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter() }


    private val job = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupCardStackView()
        setupButton()

        adapter.setSpots(createSpots())

    }

    private fun setupCardStackView() {
        initialize()
    }
    private fun initialize() {
        manager.setStackFrom(StackFrom.Left)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(4.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun setupButton() {
        val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction) {
       if(CatFactService.catPictureUrls.size < 5){
            uiScope.launch {
                CatFactService.fetchImages(applicationContext)
            }
        }
        if(CatFactService.catFacts.size < 5){
            uiScope.launch {
                CatFactService.fetchFacts(applicationContext)
            }
        }
        paginate()
    }
    private fun paginate() {

        val old = adapter.getSpots()
        val new = old.plus(createSpots())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }
    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardRewound() {
    }

    private fun createSpots(): List<Spot> {
        val spots = ArrayList<Spot>()
        for(index in 0 until 3){
            val fact: Fact = CatFactService.getFactObj()
            spots.add(Spot(fact = fact.fact, url = fact.url))
        }
        return spots
    }

}