package com.tom.spotifygamev3.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tom.spotifygamev3.LoginActivity
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.ChooseGameFragmentBinding
import com.tom.spotifygamev3.utils.Constants

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ChooseGameFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.navigateToAlbumGame.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                        Constants.ALBUM_GAME_TYPE
                    )
                )
                viewModel.onNavigateToAlbumGame()
            }
        })

        viewModel.navigateToHighLow.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                        Constants.HIGH_LOW_GAME_TYPE
                    )
                )
                viewModel.onNavigateToHighLow()
            }
        })

        viewModel.navigateToBeatIntro.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                        Constants.BEAT_INTRO_GAME_TYPE
                    )
                )
                viewModel.onNavigateToBeatIntro()
            }
        })

        viewModel.logOut.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                val googleClient = GoogleSignIn.getClient(
                    requireActivity(),
                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
                )
                googleClient.signOut().addOnCompleteListener {
                    showSignOutSnackbar(binding)
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                }

                AuthorizationClient.clearCookies(requireActivity())
            }
        })

        return binding.root

    }

    private fun showSignOutSnackbar(binding: ChooseGameFragmentBinding) {
        val green = ContextCompat.getColor(requireContext(), R.color.spotify_green)
        val white = ContextCompat.getColor(requireContext(), R.color.spotify_white)
        val snackbar = Snackbar.make(
            binding.main,
            "Signed Out!",
            Snackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(white)
        for (view in snackbar.view.allViews) {
            view.setBackgroundColor(green)
        }
        snackbar.show()
    }

}