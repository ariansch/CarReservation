package content.structure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Folder extends Content {

    private final List<Content> children = new ArrayList<>();

    public Folder(String name) {
        super(name);
    }

    public List<Content> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void add(Content child) {
        if (child == null) return;
        if (child == this) throw new IllegalArgumentException("Folder cannot be added to itself");
        if (children.contains(child)) return;
        child.getParent().ifPresent(p -> p.remove(child)); 
        child._setParentInternal(this);
        children.add(child);
    }

    @Override
    public void remove(Content child) {
        if (child == null) return;
        if (children.remove(child)) {
            child._setParentInternal(null);
        }
    }

    @Override
    public void print(StringBuilder out, int indent) {
        out.append(" ".repeat(Math.max(0, indent)))
           .append("Ordner ").append(getName())
           .append("  [Buchungen=").append(getBookingTotal())
           .append(", Zahlungen=").append(getPaymentTotal()).append("]")
           .append(System.lineSeparator());
        for (Content c : children) {
            c.print(out, indent + 2);
        }
    }

    @Override
    public BigDecimal getBookingTotal() {
        return children.stream()
                .map(Content::getBookingContribution)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getPaymentTotal() {
        return children.stream()
                .map(Content::getPaymentContribution)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
