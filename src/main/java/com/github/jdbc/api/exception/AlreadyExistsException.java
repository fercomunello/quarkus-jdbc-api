package com.github.jdbc.api.exception;

import java.util.Map;

public final class AlreadyExistsException extends RuntimeException {

    private final Map<String, Object> violations;

    public AlreadyExistsException(final Map<String, Object> violations) {
        this.violations = violations;
    }

    public Map<String, Object> getViolations() {
        return this.violations;
    }
}
