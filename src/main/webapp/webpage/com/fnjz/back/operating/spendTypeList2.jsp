<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px">
        <t:datagrid name="spendTypeList" title="支出标签管理" actionUrl="spendTypeController.do?datagrid&labelGrade=2"
                    idField="id"
                    fit="true" queryMode="group">
            <t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
            <t:dgCol title="支出类目名称" field="spendName" width="120" query="true"></t:dgCol>
            <%--<t:dgCol title="父级类目" field="parentId" width="120"></t:dgCol>--%>
            <%--<t:dgCol title="图标" field="icon" width="120"></t:dgCol>--%>
            <t:dgCol title="状态(0:下线,1:上线)" field="status" width="120" replace="下线_0,上线_1" query="true"></t:dgCol>
            <t:dgCol title="优先级" field="priority" width="120"></t:dgCol>
            <%--<t:dgCol title="常用字段,0:非常用,1:常用" field="mark" width="120" replace="常用_0,不常用_1"></t:dgCol>--%>
            <t:dgCol title="创建时间" field="createDate" formatter="yyyy-MM-dd hh:mm:ss" width="120"></t:dgCol>
            <%-- <t:dgCol title="删除标记" field="delflag" width="120"></t:dgCol>
             <t:dgCol title="删除时间" field="delDate" formatter="yyyy-MM-dd hh:mm:ss" width="120"></t:dgCol>--%>
            <t:dgCol title="操作" field="opt" width="100"></t:dgCol>
            <t:dgConfOpt url="spendTypeController.do?online&id={id}" title="上线" message="确认上线吗" urlclass="ace_button"
                         urlStyle="background-color:#1a7bb9;" urlfont="fa-database"/>
            <t:dgDelOpt title="删除" url="spendTypeController.do?del&id={id}" urlclass="ace_button" urlfont="fa-trash-o"/>
            <t:dgToolBar title="录入二级标签" icon="icon-add" url="spendTypeController.do?addorupdate&labelGrade=2"
                         funname="add"></t:dgToolBar>
            <%-- <t:dgToolBar title="录入三级标签" icon="icon-add" url="spendTypeController.do?addorupdate&labelGrade=3"
                          funname="add"></t:dgToolBar>--%>
            <t:dgToolBar title="编辑" icon="icon-edit" url="spendTypeController.do?addorupdate"
                         funname="update"></t:dgToolBar>
            <%--<t:dgToolBar title="查看" icon="icon-search" url="spendTypeController.do?addorupdate"
                         funname="detail"></t:dgToolBar>--%>
        </t:datagrid>
    </div>
</div>
