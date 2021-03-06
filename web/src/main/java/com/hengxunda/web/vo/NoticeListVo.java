package com.hengxunda.web.vo;

import com.hengxunda.dao.entity.Notice;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: lsl
 * @Date: create in 2018/6/21
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class NoticeListVo extends PageVo {

    private List<Notice> notices;
}
