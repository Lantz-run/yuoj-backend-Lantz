package com.yupi.yuoj.judge.codesandbox;

/**
 * <p>Project: yuoj-backend
 * <p>Powered by Lantz On 2025/2/8
 *
 * @author Lantz
 * @version 1.0
 * @Description CodeSandboxFactory
 * @since 1.8
 */

import com.yupi.yuoj.judge.codesandbox.impl.ExampleCodesandbox;
import com.yupi.yuoj.judge.codesandbox.impl.RemoteCodesandbox;
import com.yupi.yuoj.judge.codesandbox.impl.ThirdPartyCodesandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     * @param type
     * @return
     */
    public static CodeSandBox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodesandbox();
            case "remote":
                return new RemoteCodesandbox();
            case "thirdParty":
                return new ThirdPartyCodesandbox();
            default:
                return new ExampleCodesandbox();
        }
    }

}
