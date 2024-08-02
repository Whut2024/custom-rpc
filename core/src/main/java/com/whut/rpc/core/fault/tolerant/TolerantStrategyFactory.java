package com.whut.rpc.core.fault.tolerant;

import com.whut.rpc.core.serializer.SpiLoader;

import static com.whut.rpc.core.fault.tolerant.TolerantStrategyKeys.*;

/**
 * singleton factory
 *
 * @author whut2024
 * @since 2024-08-02
 */
public class TolerantStrategyFactory {


    static {
        SpiLoader.load(TolerantStrategy.class);
    }


    public final static TolerantStrategy DEFAULT_TOLERANT_STRATEGY = get(FAIL_OVER);


    public static TolerantStrategy get(String alias) {
        return (TolerantStrategy) SpiLoader.get(TolerantStrategy.class, alias);
    }
}
