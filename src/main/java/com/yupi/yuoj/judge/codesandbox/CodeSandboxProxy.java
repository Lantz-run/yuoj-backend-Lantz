package com.yupi.yuoj.judge.codesandbox;

import com.yupi.yuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.yuoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Project: yuoj-backend
 * <p>Powered by Lantz On 2025/2/8
 *
 * @author Lantz
 * @version 1.0
 * @Description CodeSandboxProxy
 * @since 1.8
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandBox{

    private final CodeSandBox codeSandBox;


    public CodeSandboxProxy(CodeSandBox codeSandBox){
        this.codeSandBox = codeSandBox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：" + executeCodeResponse.toString());
        return executeCodeResponse;
    }

}
