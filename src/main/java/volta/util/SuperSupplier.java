package volta.util;

import java.util.function.Supplier;

public interface SuperSupplier<T> extends Supplier<Supplier<T>> {
    default T getGet() {
        return get().get();
    }
}
