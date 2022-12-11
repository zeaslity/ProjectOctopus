package io.wdd.agent.excuetor.function;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
@Lazy
public class CollectAllFunctionToServer {


    public static Set<String> ALL_FUNCTIONS = new HashSet<>(128);

    /**
     * store the Octopus Agent Functions and Reflection Class Path
     *  key: function name
     *  value: function class relative path
     *
     */
    public static HashMap<String, String> FUNCTION_REFLECTION = new HashMap<>(128);


}
