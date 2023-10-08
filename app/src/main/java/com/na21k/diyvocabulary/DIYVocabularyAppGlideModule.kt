package com.na21k.diyvocabulary

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import java.io.InputStream

@GlideModule
class DIYVocabularyAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            com.google.firebase.storage.StorageReference::class.java,
            InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder
            .setDefaultTransitionOptions(
                Drawable::class.java,
                DrawableTransitionOptions.withCrossFade(130)
            )
            .setDiskCache(
                InternalCacheDiskCacheFactory(
                    context, 150 * 1024 * 1024  //150 MiB
                )
            )
    }

    override fun isManifestParsingEnabled(): Boolean = false
}
