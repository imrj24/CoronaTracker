package rahul.j.coronatrackerlive

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

/**
 * This will set the default toolbar with specified [title].
 * @param isBackArrowShown: suggests if the back arrow button is vissible.
 */
fun Fragment.setDefaultToolbar(
    title: String = "",
    isBackArrowShown: Boolean = true
) {
    val toolbarHandler = requireActivity() as ToolbarSettable
    val toolbarView: Toolbar = layoutInflater.inflate(
        R.layout.default_toolbar,
        toolbarHandler.toolbar,
        false
    ) as Toolbar

    if (isBackArrowShown) {
        toolbarView.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbarView.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    toolbarView.title = title
    toolbarView.setTitleTextAppearance(requireContext(), R.style.ToolbarStyle)
    toolbarHandler.toolbar.apply {
        removeAllViews()
        addView(toolbarView)
    }
}