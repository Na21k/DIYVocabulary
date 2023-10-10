package com.na21k.diyvocabulary.repositories

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.BitmapCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.na21k.diyvocabulary.WORD_IMAGES_STORAGE_FOLDER_PATH
import com.na21k.diyvocabulary.model.AttachedImageModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.roundToInt

/***
 * A valid wordId must be set to load, save, and delete image models.
 * Call resumeObservingData() to load models after you've set a wordId.
 * But you can delete by wordId even when the wordId property is not set.
 * @property wordId must be set to load, save, and delete image models.
 * But it mustn't to delete by wordId.
 */
class WordImagesRepository(application: Application) :
    ExposesModelsAsListRepository<AttachedImageModel, Task<Unit>>(application, false) {

    var wordId: String? = null
    private val mWordImagesStorageRef: StorageReference?
        get() {
            val user = mUser

            if (!ensureSignedIn(user)) {
                return null
            }

            val wordId = wordId

            if (wordId == null) {
                _error.postValue(IllegalStateException("wordId has not been set"))
                return null
            }

            return Firebase.storage.getReference(WORD_IMAGES_STORAGE_FOLDER_PATH).child(user!!.uid)
                .child(wordId)
        }
    private val mRunningUploadTasks = mutableListOf<UploadTask>()

    override fun close() {
        cancelRunningUploads()
        super.close()
    }

    override fun observeAll(): ListenerRegistration? {
        _isLoading.postValue(true)

        mWordImagesStorageRef?.listAll()
            ?.addOnCompleteListener { task ->

                _isLoading.postValue(false)

                if (task.isSuccessful) {
                    val items = task.result.items

                    val imageModels = items.map {
                        AttachedImageModel(it.name, null, it)
                    }
                    _allModels.postValue(imageModels)
                } else {
                    _error.postValue(task.exception)
                }
            }

        return null
    }

    /**
     * You must set wordId before calling this.
     * @param model the model to either add or update. Must have the deviceFileUri property set.
     * @return a Task that completes when the model has been successfully saved.
     * @see wordId
     * @see AttachedImageModel
     */
    override fun save(model: AttachedImageModel): Task<Unit> {
        val deviceFileUri = model.deviceFileUri

        if (deviceFileUri == null) {
            val ex = IllegalArgumentException("The model has no Uri string set")
            _error.postValue(ex)
            return Tasks.forException(ex)
        }

        val resTaskSource = TaskCompletionSource<Unit>()

        getBitmapFromUriStr(deviceFileUri, onLoaded = fun(bitmap) {
            val scaledCompressedImageInputStream = bitmap
                .ensureScaledAndCompressed(targetMaxDimen = 1920, compressionQuality = 30)

            val uploadTask = mWordImagesStorageRef?.child(model.fileName)
                ?.putStream(scaledCompressedImageInputStream)

            uploadTask?.let { mRunningUploadTasks += it }

            uploadTask
                ?.addOnCompleteListener { task ->
                    mRunningUploadTasks.remove(task)
                    resTaskSource.setResult(Unit)

                    if (!task.isSuccessful) {
                        _error.postValue(task.exception)
                    }
                }
        })

        return resTaskSource.task
    }

    /**
     * You must set wordId before calling this.
     * @param models the models to either add or update. Must have the deviceFileUri property set.
     * @return a Task that completes when all the models have been successfully saved.
     * @see wordId
     * @see AttachedImageModel
     */
    fun save(models: List<AttachedImageModel>): Task<Void> {
        val uploadTasks = mutableListOf<Task<Unit>>()

        models.forEach {
            uploadTasks.add(save(it))
        }

        return Tasks.whenAll(uploadTasks)
    }

    /***
     * You must set wordId before calling this.
     * Use delete(wordId: String) to delete without setting the wordId property.
     * @see wordId
     */
    override fun delete(model: AttachedImageModel) {
        if (model.downloadLinkUri == null) {
            return  //this model has never been saved
        }

        mWordImagesStorageRef?.child(model.fileName)
            ?.delete()
            ?.addOnFailureListener {
                _error.postValue(it)
            }
    }

    /***
     * Allows for deletion without setting the wordId property.
     * This does not save the supplied wordId.
     */
    fun delete(wordId: String) {
        val user = mUser

        if (!ensureSignedIn(user)) {
            return
        }

        Firebase.storage.getReference(WORD_IMAGES_STORAGE_FOLDER_PATH).child(user!!.uid)
            .child(wordId).listAll()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.items.forEach { word ->
                        word.delete()
                            .addOnFailureListener {
                                _error.postValue(it)
                            }
                    }
                } else {
                    _error.postValue(task.exception)
                }
            }
    }

    fun cancelRunningUploads() {
        mRunningUploadTasks.apply {
            forEach { it.cancel() }
            clear()
        }
    }

    private fun getBitmapFromUriStr(uri: String, onLoaded: (Bitmap) -> Unit) {
        Glide.with(application).asBitmap()
            .skipMemoryCache(true)
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onLoaded(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun Bitmap.ensureScaledAndCompressed(
        targetMaxDimen: Int,
        compressionQuality: Int
    ): InputStream {
        var imageBitmap = this
        val srcW = imageBitmap.width
        val srcH = imageBitmap.height

        val maxDimen = maxOf(srcW, srcH)

        if (maxDimen > targetMaxDimen) {
            val scaleFactor = 1f * targetMaxDimen / maxDimen

            imageBitmap = BitmapCompat.createScaledBitmap(
                imageBitmap,
                (srcW * scaleFactor).roundToInt(),
                (srcH * scaleFactor).roundToInt(),
                null, false
            )
        }

        val compressed = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, compressed)

        return ByteArrayInputStream(compressed.toByteArray())
    }
}
