package fr.feepin.justchat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.transition.Fade
import androidx.transition.TransitionManager

import fr.feepin.justchat.R
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : Fragment(R.layout.fragment_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 1000
        logoImage.startAnimation(alphaAnimation)

    }
}