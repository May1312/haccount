package com.fnjz.front.entity.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yhang on 2018/6/13.
 */
@JsonIgnoreProperties(value={"startIndex"})
public class PageRest{

    //总条数
    private int totalCount;
    //总页数
    private int totalPage;
    private int curPage = 1;
    //limit 第二个参数 每次查询条数
    private int pageSize = 10;
    //limit 第一个参数 开始查询序号
    private List<?> content;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage = this.totalCount % this.pageSize == 0 ?
                (this.totalCount / this.pageSize) : (this.totalCount/this.pageSize+1);
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartIndex() {
        if(this.curPage<0){
            return 0;
        }
        return this.pageSize * (this.curPage - 1);
    }

    public List<?> getContent() {
        return content;
    }

    public void setContent(List<?> content) {
        this.content = content;
    }
}
