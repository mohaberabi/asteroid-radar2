package com.example.nasa.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nasa.R
import com.example.nasa.databinding.FragmentAsteroidBinding
import com.example.nasa.util.NotiConst
import com.example.nasa.util.PermissionHelper
import com.example.nasa.util.showNoti
import com.example.nasa.viewmodels.AsteroidViewModel
import com.example.nasa.viewmodels.FilterEnum


class AsteroidFragment : Fragment() {
    private lateinit var asteroidViewModel: AsteroidViewModel
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var notificationManager: NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PermissionHelper(requireActivity())

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentAsteroidBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_asteroid,
                container,
                false
            )
        notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val application = requireNotNull(activity).application

        val factory = AsteroidViewModel.Factory(application)
        asteroidViewModel = ViewModelProvider(this, factory)[AsteroidViewModel::class.java]


        binding.viewModel = asteroidViewModel
        binding.lifecycleOwner = this




        asteroidViewModel.goToImageOfDay.observe(viewLifecycleOwner) {

            if (it) {

                findNavController().navigate(
                    AsteroidFragmentDirections.actionAsteroidFragmentToImageOfDayFragment(
                        asteroidViewModel.imageOfTheDay.value!!
                    )
                )
                asteroidViewModel.resetNavToImageOfDay()
            }
        }
        binding.recView.adapter = AsteroidAdapter(

            asteroidClickListener =
            AsteroidAdapter.AsteroidClickListener {

                findNavController().navigate(
                    AsteroidFragmentDirections.actionAsteroidFragmentToDetailFragment(
                        it
                    )
                )

            },
            shareListener = AsteroidAdapter.AsteroidClickListener {
                shareAsteriod(it.codename)

            },
            favoriteListenr = AsteroidAdapter.AsteroidClickListener {

                asteroidViewModel.updateAsteroidFavorite(it.id, !it.isFavorite)


                if (permissionHelper.isNotificationPermissionGranted()) {
                    val ttl = if (it.isFavorite) "Removed From Favorites" else "Added to favorite"
                    val subttl =
                        if (it.isFavorite) "You will not see it again in favorites" else "You can access ypur favorites from the menu"

                    notificationManager.showNoti(
                        title = "${it.codename} ${ttl}",
                        body = subttl,
                        appContext = requireContext(),
                    )
                } else {

                    permissionHelper.requestNotificationPermission()
                    Toast.makeText(
                        context,
                        "We Wanted to show  you notificaiotns but u did not allow please allow ",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            },
        )



        createChannel(
            NotiConst.NOTIFICATION_CHANNEL,
            NotiConst.NOTIFICATION_CHANNEL
        )
        return binding.root
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.app_description)

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var filter: FilterEnum = FilterEnum.ALL
        when (item.itemId) {
            R.id.all -> filter = FilterEnum.ALL
            R.id.today -> filter = FilterEnum.TODAY
            R.id.week -> filter = FilterEnum.WEEK
            R.id.favorite -> filter = FilterEnum.FAVORITE

        }
        asteroidViewModel.onFilterRequested(filter)
        return true
    }

    private fun shareAsteriod(name: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Look i found this asteroid on the asteroid radar app ${name}"
            )
            type = "text/plain"
        }
        context?.startActivity(Intent.createChooser(shareIntent, null))
    }
}