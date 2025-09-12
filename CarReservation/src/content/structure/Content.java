package content.structure;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class Content {

    private final String name;
    private final LocalDateTime createdAt;
    private Folder parent;

    protected Content(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public final String getName() {
        return name;
    }

    public final LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public final Optional<Folder> getParent() {
        return Optional.ofNullable(parent);
    }

    final void _setParentInternal(Folder newParent) {
        this.parent = newParent;
    }

    public abstract void print(StringBuilder out, int indent);

    public final String printTree() {
        StringBuilder s = new StringBuilder();
        print(s, 0);
        return s.toString();
    }

    public void add(Content child) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot contain children");
    }

    public void remove(Content child) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " cannot contain children");
    }

    public BigDecimal getBookingTotal()  { return BigDecimal.ZERO; }
    public BigDecimal getPaymentTotal()  { return BigDecimal.ZERO; }

    public BigDecimal getBookingContribution() { return getBookingTotal(); }
    public BigDecimal getPaymentContribution() { return getPaymentTotal(); }
}
