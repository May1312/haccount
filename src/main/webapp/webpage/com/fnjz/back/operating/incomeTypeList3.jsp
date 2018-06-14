<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/context/mytags.jsp" %>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div class="easyui-layout" fit="true">
    <div region="center" style="padding:0px;border:0px">
        <t:datagrid name="incomeTypeList" title="收入标签管理" actionUrl="incomeTypeController.do?datagrid&labelGrade=3"
                    idField="id"
                    fit="true" queryMode="group">
            <t:dgCol title="编号" field="id" hidden="true"></t:dgCol>
            <t:dgCol title="收入类目名称" field="incomeName" width="120" query="true"></t:dgCol>
            <t:dgCol title="收入父级类目" field="parentId" width="120" query="true" replace="${parentName}"></t:dgCol>
            <t:dgCol title="图标" field="icon" width="120"></t:dgCol>
            <t:dgCol title="状态(0:下线,1:上线)" field="status" width="120" replace="下线_0,上线_1" query="true"></t:dgCol>
            <t:dgCol title="优先级" field="priority" width="120"></t:dgCol>
            <t:dgCol title="常用标记,0:不常用,1:常用" field="mark" width="120" replace="常用_1,不常用_0" query="true"></t:dgCol>
            <t:dgCol title="创建时间" field="createDate" formatter="yyyy-MM-dd hh:mm:ss" width="120"></t:dgCol>
            <%--<t:dgCol title="删除标记" field="delflag" width="120"></t:dgCol>
            <t:dgCol title="删除时间" field="delDate" formatter="yyyy-MM-dd hh:mm:ss" width="120"></t:dgCol>--%>
            <t:dgCol title="操作" field="opt" width="100"></t:dgCol>


            <t:dgFunOpt funname="online(id)" title="上线" urlclass="ace_button" urlStyle="background-color:#1a7bb9;" urlfont="fa-database"></t:dgFunOpt>


            <t:dgDelOpt title="删除" url="incomeTypeController.do?del&id={id}" urlclass="ace_button"
                        urlfont="fa-trash-o"/>


            <%--<t:dgToolBar title="录入二级标签" icon="icon-add" url="incomeTypeController.do?addorupdate&labelGrade=2"
                         funname="add"></t:dgToolBar>--%>
            <t:dgToolBar title="录入三级标签" icon="icon-add" url="incomeTypeController.do?addorupdate&labelGrade=3"
                         funname="add"></t:dgToolBar>


            <t:dgToolBar title="编辑" icon="icon-edit" url="incomeTypeController.do?addorupdate"
                         funname="update"></t:dgToolBar>
            <%--<t:dgToolBar title="查看" icon="icon-search" url="incomeTypeController.do?addorupdate"
                         funname="detail"></t:dgToolBar>--%>
        </t:datagrid>
    </div>
</div>

<script>
    function online(id) {
        $.ajax({
            url: 'incomeTypeController.do?online',
            data: {'id': id},
            dataType: 'JSON',
            type: 'post',
            error: erryFunction,  //错误执行方法
            success: succFunction //成功执行方法
        });

        function erryFunction() {
            alert("error");
        }

        function succFunction(j) {
            alert(j.msg);
            window.location.reload();
        }
    }




    function test() {
        alert("你点击了确定,重新进行认证");
    }

    //也可以传方法名 test
    $("#update").bind("click", function () {
        $.MsgBox.Confirm("温馨提示", "确定要进行修改吗？", test);
    });


</script>