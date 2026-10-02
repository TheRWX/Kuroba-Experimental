package com.github.k1rakishou.chan.ui.controller.popup

import android.content.Context
import androidx.annotation.CallSuper
import androidx.compose.runtime.mutableStateOf
import com.github.k1rakishou.chan.core.base.DebouncingCoroutineExecutor
import com.github.k1rakishou.chan.core.base.RendezvousCoroutineExecutor
import com.github.k1rakishou.chan.core.compose.AsyncData
import com.github.k1rakishou.chan.core.helper.PostHideHelper
import com.github.k1rakishou.chan.core.manager.ChanThreadManager
import com.github.k1rakishou.chan.core.parser.PostViewMode
import com.github.k1rakishou.chan.core.manager.ChanThreadViewableInfoManager
import com.github.k1rakishou.chan.core.manager.PostFilterHighlightManager
import com.github.k1rakishou.chan.core.manager.PostFilterManager
import com.github.k1rakishou.chan.core.manager.PostHideManager
import com.github.k1rakishou.chan.core.manager.PostHighlightManager
import com.github.k1rakishou.chan.core.manager.SavedReplyManager
import com.github.k1rakishou.chan.ui.cell.PostCellInterface
import com.github.k1rakishou.chan.ui.compose.post.state.PostDisplayOptions
import com.github.k1rakishou.chan.ui.compose.post.state.ThreadCellStateDependenciesImpl
import com.github.k1rakishou.chan.ui.compose.post.state.ThreadState
import com.github.k1rakishou.chan.ui.controller.BaseFloatingComposeController
import com.github.k1rakishou.chan.ui.helper.PostPopupHelper
import com.github.k1rakishou.chan.ui.view.ThumbnailView
import com.github.k1rakishou.model.data.descriptor.ChanDescriptor
import com.github.k1rakishou.model.data.descriptor.PostDescriptor
import com.github.k1rakishou.model.data.post.ChanPost
import com.github.k1rakishou.model.data.post.ChanPostImage
import dagger.Lazy
import javax.inject.Inject

abstract class BasePostPopupController<T : PostPopupHelper.PostPopupData>(
  context: Context,
  protected val postPopupHelper: PostPopupHelper,
  protected val postCellCallback: PostCellInterface.PostCellCallback
) : BaseFloatingComposeController(context) {

  @Inject
  lateinit var postFilterManager: Lazy<PostFilterManager>
  @Inject
  lateinit var savedReplyManager: Lazy<SavedReplyManager>
  @Inject
  lateinit var postFilterHighlightManager: Lazy<PostFilterHighlightManager>
  @Inject
  lateinit var chanThreadViewableInfoManager: Lazy<ChanThreadViewableInfoManager>
  @Inject
  lateinit var postHideManager: Lazy<PostHideManager>
  @Inject
  lateinit var postHideHelper: Lazy<PostHideHelper>
  @Inject
  lateinit var chanThreadManager: Lazy<ChanThreadManager>
  @Inject
  lateinit var postHighlightManager: PostHighlightManager

  abstract val postPopupType: PostPopupType
  abstract val postDisplayOptions: PostDisplayOptions

  protected val rendezvousCoroutineExecutor = RendezvousCoroutineExecutor(controllerScope)
  protected val debouncingCoroutineExecutor = DebouncingCoroutineExecutor(controllerScope)

  protected val displayingAsyncDataState = mutableStateOf<AsyncData<T>>(AsyncData.Loading)
  protected val displayingData: T?
    get() = (displayingAsyncDataState.value as? AsyncData.Data<T>)?.data

  protected val threadState by lazy(LazyThreadSafetyMode.NONE) {
    ThreadState(
      dependencies = ThreadCellStateDependenciesImpl(controllerScope),
      initialWindowSize = 32,
      controllerKey = controllerKey,
      postDisplayOptions = postDisplayOptions,
      postCellCallback = postCellCallback
    )
  }

  override fun onCreate() {
    super.onCreate()

    onThemeChanged()
  }

  override fun onShow() {
    super.onShow()
    onThemeChanged()
  }

  // TODO: compose post cells.
  private fun onThemeChanged() {
//    val isDarkColor = ThemeEngine.isDarkColor(themeEngine.chanTheme.backColor)
//    val backDrawable = themeEngine.getDrawableTinted(context, R.drawable.ic_arrow_back_white_24dp, isDarkColor)
//    val doneDrawable = themeEngine.getDrawableTinted(context, R.drawable.ic_done_white_24dp, isDarkColor)

  }

  fun resetCachedPostData(postDescriptors: Collection<PostDescriptor>) {
    // In Compose UI, post data calculation is refreshed on updateAllPosts / onPostsUpdated
  }

  fun getThumbnail(postImage: ChanPostImage): ThumbnailView? {
    // In Compose UI, thumbnails are handled by Coil ImageLoader / PostCellMediaState
    return null
  }

  suspend fun updateAllPosts(chanDescriptor: ChanDescriptor) {
    val postDescriptors = getDisplayingPostDescriptors()
    if (postDescriptors.isEmpty()) {
      return
    }

    val updatedPosts = chanThreadManager.get().getPosts(postDescriptors)
    if (updatedPosts.isEmpty()) {
      return
    }

    onPostsUpdated(updatedPosts)
  }

  suspend fun onPostsUpdated(updatedPosts: List<ChanPost>) {
    val currentChanDescriptor = displayingData?.descriptor ?: return
    threadState.updatePosts(
      chanDescriptor = currentChanDescriptor,
      posts = updatedPosts,
      postViewMode = PostViewMode.List,
      preloadStartPosition = 0,
      forced = true
    )
  }

  @CallSuper
  open fun displayData(chanDescriptor: ChanDescriptor, data: PostPopupHelper.PostPopupData) {
    cleanup()
    displayingAsyncDataState.value = AsyncData.Data(data as T)
  }

  fun scrollTo(displayPosition: Int) {
    // Handled by LazyListState in Compose Content()
  }

  override fun onOutsideOfDialogClicked() {
    postPopupHelper.pop()
  }

  override fun onBack(): Boolean {
    postPopupHelper.pop()
    return true
  }

  abstract fun cleanup()
  abstract fun getDisplayingPostDescriptors(): List<PostDescriptor>
  abstract fun onImageIsAboutToShowUp()

  enum class PostPopupType {
    Replies,
    Search
  }
}