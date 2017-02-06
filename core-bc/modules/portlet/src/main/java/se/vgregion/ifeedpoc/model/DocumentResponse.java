package se.vgregion.ifeedpoc.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Patrik Björk
 */
public class DocumentResponse implements Serializable {

    private static final long serialVersionUID = -3957962872398844707L;

    private byte[] bytes;
    private String contentType;

    public DocumentResponse(byte[] bytes, String contentType) {
        this.bytes = bytes;
        this.contentType = contentType;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentResponse that = (DocumentResponse) o;

        if (!Arrays.equals(bytes, that.bytes)) return false;
        return contentType.equals(that.contentType);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(bytes);
        result = 31 * result + contentType.hashCode();
        return result;
    }
}
