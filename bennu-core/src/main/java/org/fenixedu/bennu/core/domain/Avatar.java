package org.fenixedu.bennu.core.domain;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.util.CoreConfiguration;

public class Avatar extends Avatar_Base {
    protected Avatar(byte[] data, String mimeType) {
        super();
        setData(data);
        setMimeType(mimeType);
    }

    public static Avatar create(byte[] src, String mimeType) {
        return new Avatar(src, mimeType);
    }

    public static Avatar crop(byte[] src, String mimeType, int x1, int y1, int x2, int y2) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(src));
            if (img != null) {
                BufferedImage crop = img.getSubimage(x1, y1, x2 - x1, y2 - y1);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType).next();
                try (ImageOutputStream outstream = ImageIO.createImageOutputStream(out)) {
                    writer.setOutput(outstream);
                    writer.write(crop);
                    return new Avatar(out.toByteArray(), mimeType);
                }
            }
            throw BennuCoreDomainException.errorProcessingImage();
        } catch (IOException e) {
            throw BennuCoreDomainException.errorProcessingImage();
        }
    }

    public byte[] getData(int size) {
        try (InputStream stream = new ByteArrayInputStream(getData())) {
            return process(stream, getMimeType(), size);
        } catch (IOException e) {
            throw BennuCoreDomainException.errorProcessingImage();
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

    public static Avatar getForUser(User user) {
        return user.getProfile().getLocalAvatar();
    }

    public static String mysteryManUrl(User user) {
        return CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-core/profile/localavatar/"
                + user.getUsername();
    }

    public static byte[] process(InputStream stream, String mimeType, int size) {
        try {
            BufferedImage img = ImageIO.read(stream);
            if (img == null) {
                throw BennuCoreDomainException.errorProcessingImage();
            }
            BufferedImage scaled = scale(img, size);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageWriter writer = ImageIO.getImageWritersByMIMEType(mimeType).next();
            try (ImageOutputStream outstream = ImageIO.createImageOutputStream(out)) {
                writer.setOutput(outstream);
                writer.write(scaled);
                return out.toByteArray();
            }
        } catch (IOException e) {
            throw BennuCoreDomainException.errorProcessingImage();
        }
    }

    private static BufferedImage scale(BufferedImage src, int size) {
        BufferedImage result = new BufferedImage(size, size,
                (src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB));
        Graphics2D resultGraphics = result.createGraphics();
        resultGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        resultGraphics.drawImage(src, 0, 0, size, size, null);
        resultGraphics.dispose();
        return result;
    }
}
