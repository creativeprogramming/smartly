package org.smartly.packages.htmldeployer.impl.compilers;

import org.lesscss.LessCompiler;
import org.smartly.Smartly;
import org.smartly.commons.lang.compilers.ICompiler;

import java.util.Map;

/**
 * LessCompiler Wrapper
 */
public class CompilerLess implements ICompiler {

    private LessCompiler _native;

    public CompilerLess() {
        _native = new LessCompiler();
    }

    @Override
    public byte[] compile(byte[] data) throws Exception {
        return compile(data, null);
    }

    @Override
    public byte[] compile(byte[] data, final Map<String, Object> args) throws Exception {
        final String input = new String(data, Smartly.getCharset());
        return _native.compile(input).getBytes();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
