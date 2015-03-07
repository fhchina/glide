package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;

import java.security.MessageDigest;

/**
 * Transforms {@link android.graphics.drawable.BitmapDrawable}s.
 */
public class BitmapDrawableTransformation implements Transformation<BitmapDrawable> {

  private final Context context;
  private final BitmapPool bitmapPool;
  private final Transformation<Bitmap> wrapped;

  public BitmapDrawableTransformation(Context context, Transformation<Bitmap> wrapped) {
    this(context, Glide.get(context).getBitmapPool(), wrapped);
  }

  // Visible for testing.
  BitmapDrawableTransformation(Context context, BitmapPool bitmapPool,
      Transformation<Bitmap> wrapped) {
    this.context = context.getApplicationContext();
    this.bitmapPool = Preconditions.checkNotNull(bitmapPool);
    this.wrapped = Preconditions.checkNotNull(wrapped);
  }

  @Override
  public Resource<BitmapDrawable> transform(Resource<BitmapDrawable> resource, int outWidth,
      int outHeight) {
    BitmapDrawable other = resource.get();
    Bitmap bitmap = other.getBitmap();
    BitmapResource toTransform = BitmapResource.obtain(bitmap, bitmapPool);
    Resource<Bitmap> result = wrapped.transform(toTransform, outWidth, outHeight);
    if (!result.equals(toTransform)) {
      toTransform.recycle();
    }
    return new BitmapDrawableResource(new BitmapDrawable(context.getResources(), result.get()),
        bitmapPool);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BitmapDrawableTransformation) {
      BitmapDrawableTransformation other = (BitmapDrawableTransformation) o;
      return wrapped.equals(other.wrapped);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return wrapped.hashCode();
  }

  @Override
  public void updateDiskCacheKey(MessageDigest messageDigest) {
    wrapped.updateDiskCacheKey(messageDigest);
  }
}
