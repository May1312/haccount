package com.fnjz.front.entity.api;

import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhang on 2018/6/13.
 */
public class PageRest implements Serializable {

    // 总条数
    private Integer totalCount;

    // 当前页序号：从1开始计数
    private Integer curpage = 1;

    // 每页最大条数
    private Integer itemPerPage = 10;

    // 当前页条数:如果是最后一页，其值小于等于itemPerPage
    private Integer itemCurPage;

    // 总页数
    private Integer pageCount;

    private List<T> content = new ArrayList<>();

    /**
     * 根据当前页序号和每页最大条数取得偏移量，即数据库的offset
     * @return offset
     */
    public Integer getOffset() {
        return (curpage-1) * itemPerPage;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        if (totalCount % this.itemPerPage == 0) {
            this.pageCount = totalCount / this.itemPerPage;
        } else {
            this.pageCount = totalCount / this.itemPerPage + 1;
        }
    }

    public Integer getCurpage() {
        if(curpage>0){
            return curpage-1;
        }
        return curpage;
    }

    public void setCurpage(Integer curpage) {
        this.curpage = curpage;
    }

    public Integer getItemPerPage() {
        return itemPerPage;
    }

    public void setItemPerPage(Integer itemPerPage) {
        this.itemPerPage = itemPerPage;
    }

    public Integer getItemCurPage() {
        return itemCurPage;
    }

    public void setItemCurPage(Integer itemCurPage) {
        this.itemCurPage = itemCurPage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PageRest{" +
                "totalCount=" + totalCount +
                ", curpage=" + curpage +
                ", itemPerPage=" + itemPerPage +
                ", itemCurPage=" + itemCurPage +
                ", pageCount=" + pageCount +
                ", content=" + content +
                '}';
    }
}
