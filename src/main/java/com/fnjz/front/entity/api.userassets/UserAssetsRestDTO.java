package com.fnjz.front.entity.api.userassets;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @Title: Entity
 * @Description: 用户资产相关
 * @date 2018-10-20 11:11:26
 * @version V1.0
 *
 */

public class UserAssetsRestDTO implements java.io.Serializable {

    /**资产类型 当类型为1时 此字段有意义*/
    private java.lang.Integer assetsType;
    /**金额*/
    private BigDecimal money;
    /**更新时间*/
    private java.util.Date updateDate;
    /**创建日期*/
    private java.util.Date createDate;

    /**
     *方法: 取得java.util.Date
     *@return: java.util.Date  资产类型 当类型为1时 此字段有意义
     */
    @Column(name ="ASSETS_TYPE",nullable=true)
    public java.lang.Integer getAssetsType(){
        return this.assetsType;
    }

    /**
     *方法: 设置java.util.Date
     *@param: java.util.Date  资产类型 当类型为1时 此字段有意义
     */
    public void setAssetsType(java.lang.Integer assetsType){
        this.assetsType = assetsType;
    }
    /**
     *方法: 取得BigDecimal
     *@return: BigDecimal  金额
     */
    @Column(name ="MONEY")
    public BigDecimal getMoney(){
        return this.money;
    }

    /**
     *方法: 设置BigDecimal
     *@param: BigDecimal  金额
     */
    public void setMoney(BigDecimal money){
        this.money = money;
    }

    /**
     *方法: 取得java.util.Date
     *@return: java.util.Date  更新时间
     */
    @Column(name ="UPDATE_DATE",nullable=true)
    public java.util.Date getUpdateDate(){
        return this.updateDate;
    }

    /**
     *方法: 设置java.util.Date
     *@param: java.util.Date  更新时间
     */
    public void setUpdateDate(java.util.Date updateDate){
        this.updateDate = updateDate;
    }
    /**
     *方法: 取得java.util.Date
     *@return: java.util.Date  创建日期
     */
    @Column(name ="CREATE_DATE",nullable=true)
    public java.util.Date getCreateDate(){
        return this.createDate;
    }

    /**
     *方法: 设置java.util.Date
     *@param: java.util.Date  创建日期
     */
    public void setCreateDate(java.util.Date createDate){
        this.createDate = createDate;
    }
}
