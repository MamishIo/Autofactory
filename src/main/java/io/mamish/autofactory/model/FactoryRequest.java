package io.mamish.autofactory.model;

import java.util.List;

public record FactoryRequest(
        List<ProductAmount> desiredOutput
) { }
