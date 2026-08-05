package net.arthurllew.betadeco.core.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WallpaperVariant(byte value) {
    /**
     * File writing codec.
     */
    public static final Codec<WallpaperVariant> CODEC =
            Codec.BYTE.xmap(WallpaperVariant::new, WallpaperVariant::value);
    /**
     * Network codec.
     */
    public static final StreamCodec<ByteBuf, WallpaperVariant> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(WallpaperVariant::new, WallpaperVariant::value);

    /**
     * @return {@code true} (objects are equal regardless of value).
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof WallpaperVariant;
    }
}
