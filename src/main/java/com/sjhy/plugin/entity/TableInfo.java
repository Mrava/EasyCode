package com.sjhy.plugin.entity;

import com.intellij.database.psi.DbTable;
import lombok.Data;

import java.util.List;

/**
 * 表信息
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class TableInfo {
    /**
     * 原始对象
     */
    private DbTable obj;
    /**
     * 表名（首字母大写）
     */
    private String name;
    /**
     * 注释
     */
    private String comment;
    /**
     * 所有列
     */
    private List<ColumnInfo> fullColumn;
    /**
     * 主键列
     */
    private List<ColumnInfo> pkColumn;
    /**
     * 其他列
     */
    private List<ColumnInfo> otherColumn;
    /**
     * 保存的包名称
     */
    private String savePackageName;
    /**
     * 保存路径
     */
    private String savePath;
    /**
     * html文件保存路劲
     */
    private String htmlSavePath;
    /**
     * 保存的model名称
     */
    private String saveModelName;

}
