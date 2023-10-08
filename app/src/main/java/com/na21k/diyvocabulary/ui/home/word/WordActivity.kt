package com.na21k.diyvocabulary.ui.home.word

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.na21k.diyvocabulary.OPEN_IMAGE_TMP_FILE_NAME
import com.na21k.diyvocabulary.R
import com.na21k.diyvocabulary.WORD_ATTACHED_IMAGES_COUNT_MAX
import com.na21k.diyvocabulary.databinding.ActivityWordBinding
import com.na21k.diyvocabulary.databinding.TagChipViewBinding
import com.na21k.diyvocabulary.helpers.setTextIfEmpty
import com.na21k.diyvocabulary.helpers.showErrorSnackbar
import com.na21k.diyvocabulary.helpers.showSnackbar
import com.na21k.diyvocabulary.helpers.showToast
import com.na21k.diyvocabulary.model.AttachedImageModel
import com.na21k.diyvocabulary.model.TagModel
import com.na21k.diyvocabulary.model.WordModel
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.AttachedImagesListAdapter
import com.na21k.diyvocabulary.ui.home.word.attachedImagesList.OnImageActionRequestedListener
import com.na21k.diyvocabulary.ui.home.word.pickTagDialog.PickTagDialogFragment
import com.na21k.diyvocabulary.ui.shared.BaseActivity
import com.na21k.diyvocabulary.ui.tags.tagDialog.TagDialogFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.util.Date

const val WORD_MODEL_ARG_KEY = "wordModelArgKey"

class WordActivity : BaseActivity(), PickTagDialogFragment.OnPickTagDialogFragmentActionListener,
    TagDialogFragment.OnTagDialogFragmentActionListener {

    private lateinit var mBinding: ActivityWordBinding
    private lateinit var mViewModel: WordActivityViewModel
    private lateinit var mImagesListAdapter: AttachedImagesListAdapter
    private val onImageActionRequestedListener = object : OnImageActionRequestedListener {
        override fun onImageOpenRequested(image: AttachedImageModel) {
            saveImageToFileAndOpen(image)
        }

        override fun onImageDeletionRequested(image: AttachedImageModel) {
            mImagesListAdapter.deleteItem(image)
            mViewModel.deleteImage(image)
        }

        override fun onImageAdditionRequested() {
            if (mViewModel.isLoading.value == true) {
                showSnackbar(mBinding.root, R.string.loading_images_snack)
                return
            }
            if (mViewModel.finalAttachedImagesCount >= WORD_ATTACHED_IMAGES_COUNT_MAX) {
                val message = getString(
                    R.string.cant_attach_more_images_formatted_snack,
                    WORD_ATTACHED_IMAGES_COUNT_MAX
                )
                Snackbar.make(mBinding.root, message, Snackbar.LENGTH_LONG).show()
                return
            }

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            val mimeTypes = arrayOf("image/png", "image/jpeg")
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            mOpenImageActivityResultLauncher.launch(intent)
        }
    }
    private lateinit var mOpenImageActivityResultLauncher: ActivityResultLauncher<Intent>
    private val wordExtra: WordModel?
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(WORD_MODEL_ARG_KEY, WordModel::class.java)
        } else {
            intent.extras?.getSerializable(WORD_MODEL_ARG_KEY) as WordModel?
        }
    private val isExistingDocument get() = wordExtra != null
    private var mWord = WordModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.appBar.appBar)

        mViewModel = ViewModelProvider(this)[WordActivityViewModel::class.java]

        onBackPressedDispatcher.addCallback(this) {
            if (mViewModel.imagesUploadTask.value == null) finish()
            else showUploadSnackbar()
        }

        enableUpNavigation(mBinding.appBar.appBar)

        mImagesListAdapter = setUpImagesList()
        displayIfExistingDocument()
        registerForImageSelectionActivityResult()
        displayTags()
        observeLiveData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.word_activity_options_menu, menu)

        if (!isExistingDocument) {
            menu?.removeItem(R.id.delete)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                save()
                true
            }

            R.id.delete -> {
                delete()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayIfExistingDocument() {
        if (!isExistingDocument) {
            return
        }

        val word = wordExtra

        mWord = word!!
        setTextIfEmpty(mBinding.word, word.word)
        setTextIfEmpty(mBinding.transcription, word.transcription)
        setTextIfEmpty(mBinding.translation, word.translation)
        setTextIfEmpty(mBinding.explanation, word.explanation)
        setTextIfEmpty(mBinding.usageExample, word.usageExample)
        mBinding.lastModified.text = getString(
            R.string.last_modified_formatted,
            word.lastModified?.toDate()?.let { DateFormat.getDateTimeInstance().format(it) }
        )

        mBinding.lastModified.visibility = View.VISIBLE

        word.id?.let { mViewModel.loadAttachedImages(it) }
    }

    private fun setUpImagesList(): AttachedImagesListAdapter {
        val rv = mBinding.includedImagesList.imagesList
        val adapter = AttachedImagesListAdapter(onImageActionRequestedListener)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        return adapter
    }

    override fun observeLiveData() {
        mViewModel.error.observe(this) {
            if (it != null) {
                showErrorSnackbar(mBinding.root, it.message.toString())
                mViewModel.consumeError()
            }
        }
        mViewModel.isLoading.observe(this) {
            mBinding.attachedImagesProgressBar.isVisible = it
        }
        mViewModel.attachedImages.observe(this) {
            mImagesListAdapter.setItems(it)
        }
        mViewModel.imagesUploadTask.observe(this) {
            it?.addOnCompleteListener { finish() }
        }
    }

    private fun displayTags() {
        mBinding.tags.removeAllViews()

        mWord.tagModels?.forEach { tagModel ->
            val tagChipBinding = TagChipViewBinding.inflate(layoutInflater)
            tagChipBinding.root.text = tagModel.title
            tagChipBinding.root.closeIcon =
                AppCompatResources.getDrawable(this, R.drawable.ic_remove_24)
            tagChipBinding.root.isCloseIconVisible = true

            tagChipBinding.root.setOnCloseIconClickListener {
                removeTag(tagModel, tagChipBinding.root)
            }

            mBinding.tags.addView(tagChipBinding.root)
        }

        displayAddTagChip()
    }

    private fun removeTag(tagModel: TagModel, tagChip: Chip) {
        mWord.removeTag(tagModel)
        mBinding.tags.removeView(tagChip)
    }

    private fun displayAddTagChip() {
        val addTagChipBinding = TagChipViewBinding.inflate(layoutInflater)
        addTagChipBinding.root.text = getString(R.string.add_tag_chip)
        addTagChipBinding.root.chipIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_add_24)

        addTagChipBinding.root.setOnClickListener {
            val pickTagDialog = PickTagDialogFragment()
            pickTagDialog.show(supportFragmentManager, null)
        }

        mBinding.tags.addView(addTagChipBinding.root)
    }

    private fun save() {
        if (mBinding.word.text.isNullOrBlank()) {
            mBinding.wordLayout.error = getString(R.string.validation_required)
            return
        }

        mWord.word = mBinding.word.text.toString()
        mWord.transcription = mBinding.transcription.text.toString()
        mWord.translation = mBinding.translation.text.toString()
        mWord.explanation = mBinding.explanation.text.toString()
        mWord.usageExample = mBinding.usageExample.text.toString()

        mViewModel.save(mWord)
        switchLoadingMode(true, mBinding.attachedImagesProgressBar)
        scrollToTop()
    }

    private fun delete() {
        mViewModel.delete(mWord)
        finish()
    }

    private fun scrollToTop() {
        mBinding.scrollView.smoothScrollTo(0, 0)
    }

    private fun showUploadSnackbar() {
        Snackbar
            .make(
                mBinding.root,
                R.string.uploading_images_please_wait_snack,
                Snackbar.LENGTH_INDEFINITE
            )
            .setAction(android.R.string.cancel) { cancelRunningImageUploads() }
            .show()
    }

    private fun cancelRunningImageUploads() = mViewModel.cancelRunningImageUploads()

    override fun onPicked(tagModel: TagModel) {
        mWord.addTag(tagModel)
        displayTags()
    }

    override fun onSaveTag(tag: TagModel) {
        mViewModel.saveTag(tag)
    }

    override fun onDeleteTag(tag: TagModel) {
        //no implementation required, the user can't open existing tags from this activity
    }

    private fun saveImageToFileAndOpen(image: AttachedImageModel) {
        val file = File(cacheDir, OPEN_IMAGE_TMP_FILE_NAME)

        try {
            file.delete()

            val out = FileOutputStream(file)

            Glide.with(this).asBitmap()
                .load(image.deviceFileUri ?: image.downloadLinkUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.close()

                        openImageFile(file)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } catch (e: FileNotFoundException) {
            showErrorSnackbar(mBinding.root, R.string.unexpected_error_occurred)
        } catch (e: IOException) {
            showErrorSnackbar(mBinding.root, R.string.unexpected_error_occurred)
        } catch (e: SecurityException) {
            showErrorSnackbar(mBinding.root, R.string.unexpected_error_occurred)
        }
    }

    private fun openImageFile(file: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, "image/jpeg")
            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(intent)
    }

    private fun registerForImageSelectionActivityResult() {
        mOpenImageActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                showToast(this, R.string.importing_image_toast, Toast.LENGTH_SHORT)

                val uriStr = result.data?.data?.toString()

                if (uriStr == null) {
                    showErrorSnackbar(mBinding.root, R.string.unexpected_error_occurred)
                    return@registerForActivityResult
                }

                val image = AttachedImageModel(Date().time.toString(), uriStr, null)
                val attached = mViewModel.attachImage(image)

                if (attached) {
                    mImagesListAdapter.addItem(image)
                } else {
                    showErrorSnackbar(mBinding.root, R.string.unexpected_error_occurred)
                }
            }
        }
    }

    override fun disableButtons() {
        mBinding.word.isEnabled = false
        mBinding.transcription.isEnabled = false
        mBinding.translation.isEnabled = false
        mBinding.explanation.isEnabled = false
        mBinding.usageExample.isEnabled = false
        mBinding.tags.children.forEach { it.isEnabled = false }
        mBinding.appBar.appBar.menu.children.forEach { it.isEnabled = false }
    }

    override fun enableButtons() {
        mBinding.word.isEnabled = true
        mBinding.transcription.isEnabled = true
        mBinding.translation.isEnabled = true
        mBinding.explanation.isEnabled = true
        mBinding.usageExample.isEnabled = true
        mBinding.tags.children.forEach { it.isEnabled = true }
        mBinding.appBar.appBar.menu.children.forEach { it.isEnabled = true }
    }
}
