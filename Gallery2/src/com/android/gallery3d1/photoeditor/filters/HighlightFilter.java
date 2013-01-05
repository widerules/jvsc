/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d1.photoeditor.filters;

import android.media.effect.Effect;
import android.media.effect.EffectFactory;

import com.android.gallery3d1.photoeditor.Photo;

/**
 * Highlight filter applied to the image.
 */
public class HighlightFilter extends AbstractScaleFilter {

    public static final Creator<HighlightFilter> CREATOR = creatorOf(HighlightFilter.class);

    @Override
    public void process(Photo src, Photo dst) {
        Effect effect = getEffect(EffectFactory.EFFECT_BLACKWHITE);
        effect.setParameter("black", 0f);
        effect.setParameter("white", 1f - scale * 0.5f);
        effect.apply(src.texture(), src.width(), src.height(), dst.texture());
    }
}