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

/**
 * 题目用例
 */
@Data
public class JudgeCase {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;

}
