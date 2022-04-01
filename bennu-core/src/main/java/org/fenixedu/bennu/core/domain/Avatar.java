package org.fenixedu.bennu.core.domain;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.util.CoreConfiguration;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class Avatar extends Avatar_Base {

    public interface PhotoProvider {
        String refreshID();
        String getMimeType();
        byte[] getCustomAvatar(int width, int height, String pictureMode);
    }

    public interface ImageProcessor {

        default byte[] process(final InputStream stream, final String mimeType, final int size) {
            try {
                final BufferedImage img = ImageIO.read(stream);
                if (img == null) {
                    throw BennuCoreDomainException.errorProcessingImage("image.is.null");
                }
                final BufferedImage scaled = scale(img, size);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType).next();
                try (final ImageOutputStream outstream = ImageIO.createImageOutputStream(out)) {
                    writer.setOutput(outstream);
                    writer.write(scaled);
                    return out.toByteArray();
                }
            } catch (final IOException e) {
                throw BennuCoreDomainException.errorProcessingImage(e);
            }
        }

    }

    public static ImageProcessor imageProcessorFunction = new ImageProcessor() { };

    public static Function<User, PhotoProvider> photoProvider = user -> {
        final UserProfile userProfile = user.getProfile();
        final Avatar avatar = userProfile == null ? null : userProfile.getLocalAvatar();
        return avatar == null ? null : new PhotoProvider() {
            @Override
            public String getMimeType() {
                return avatar.getMimeType();
            }

            @Override
            public byte[] getCustomAvatar(final int width, final int height, final String pictureMode) {
                return avatar.getData(width);
            }

            @Override
            public String refreshID() {
                return avatar.getExternalId(); }
        };
    };

    protected Avatar(final byte[] data, final String mimeType) {
        super();
        setData(data);
        setMimeType(mimeType);
    }

    public static Avatar create(final byte[] src, final String mimeType) {
        return new Avatar(src, mimeType);
    }

    public static Avatar crop(final byte[] src, final String mimeType,
                              final int x1, final int y1, final int x2, final int y2) {
        try {
            final BufferedImage img = ImageIO.read(new ByteArrayInputStream(src));
            if (img != null) {
                final BufferedImage crop = img.getSubimage(x1, y1, x2 - x1, y2 - y1);
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType).next();
                try (final ImageOutputStream outstream = ImageIO.createImageOutputStream(out)) {
                    writer.setOutput(outstream);
                    writer.write(crop);
                    return new Avatar(out.toByteArray(), mimeType);
                }
            }
            throw BennuCoreDomainException.errorProcessingImage("image.is.null");
        } catch (final IOException e) {
            throw BennuCoreDomainException.errorProcessingImage(e);
        }
    }

    public byte[] getData(final int size) {
        try (final InputStream stream = new ByteArrayInputStream(getData())) {
            return process(stream, getMimeType(), size);
        } catch (final IOException e) {
            throw BennuCoreDomainException.errorProcessingImage(e);
        }
    }

    @Override
    public String getMimeType() {
        // FIXME: remove when framework support read-only slots
        return super.getMimeType();
    }

    public String url() {
        return CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-core/profile/localavatar/"
                + getProfile().getUser().getUsername();
    }

    public void delete() {
        setProfile(null);
        deleteDomainObject();
    }

    public static Avatar getForUser(final User user) {
        return user.getProfile().getLocalAvatar();
    }

    public static String mysteryManUrl(final User user) {
        return CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-core/profile/localavatar/"
                + user.getUsername();
    }

    public static byte[] process(final InputStream stream, final String mimeType, final int size) {
        return imageProcessorFunction.process(stream, mimeType, size);
    }

    private static BufferedImage scale(final BufferedImage src, final int size) {
        final BufferedImage result = new BufferedImage(size, size,
                (src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB));
        final Graphics2D resultGraphics = result.createGraphics();
        resultGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        resultGraphics.drawImage(src, 0, 0, size, size, null);
        resultGraphics.dispose();
        return result;
    }

}
