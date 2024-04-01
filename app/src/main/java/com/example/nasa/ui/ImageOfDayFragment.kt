package com.example.nasa.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.nasa.R
import com.example.nasa.databinding.FragmentImageOfDayBinding


class ImageOfDayFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentImageOfDayBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_image_of_day,
                container,
                false
            )

        val image = ImageOfDayFragmentArgs.fromBundle(requireArguments()).image
        binding.imageOfDay = image
        return binding.root
    }

}