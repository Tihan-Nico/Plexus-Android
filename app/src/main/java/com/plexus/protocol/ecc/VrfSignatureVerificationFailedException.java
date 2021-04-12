package com.plexus.protocol.ecc;

public class VrfSignatureVerificationFailedException extends Exception {

    public VrfSignatureVerificationFailedException() {
        super();
    }

    public VrfSignatureVerificationFailedException(String message) {
        super(message);
    }

    public VrfSignatureVerificationFailedException(Exception exception) {
        super(exception);
    }
}
