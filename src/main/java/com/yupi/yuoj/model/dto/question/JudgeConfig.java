package com.yupi.yuoj.model.dto.question;

import lombok.Data;

/**
 * <p>Project: yuoj-backend
 * <p>Powered by Lantz On 2025/1/15
 *
 * @author Lantz
 * @version 1.0
 * @Description JudgeCase
 * @since 1.8
 */
@Data
public class JudgeConfig {

    /**
     * 时间限制 (ms)
     */
    private Long timeLimit;

    /**
     * 内存限制 (KB)
     */
    private Long memoryLimit;

    /**
     * 堆栈限制 (KB)
     */
    private Long stackLimit;

}
