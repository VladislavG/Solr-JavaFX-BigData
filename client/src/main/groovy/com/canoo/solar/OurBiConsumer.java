package com.canoo.solar;

public interface OurBiConsumer<F, S> {
    void accept(F first, S second);
}

