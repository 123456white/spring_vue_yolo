<template>
	<!--根标签-->
	<div>
		<!-- 包含搜索相关的输入框和按钮。 -->
		<div style="padding: 10px 0;">
		  <!-- 输入用户名的输入框，带有搜索图标，双向绑定到username变量 -->
		  <el-input style="width: 200px;" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="username"></el-input>
		  <el-input style="margin-left: 5px;width: 200px;" placeholder="请输入邮箱" suffix-icon="el-icon-message" v-model="email"></el-input>
		  <el-input style="margin-left: 5px;width: 200px;" placeholder="请输入电话" suffix-icon="el-icon-phone" v-model="phone"></el-input>
		  <!-- 搜索按钮，点击触发load方法 -->
		  <el-button style="margin-left: 5px;" type="primary" @click="load">搜索</el-button>
		  <!-- 重置按钮，点击触发reset方法 -->
		  <el-button type="warning" @click="reset">重置</el-button>
		</div>
		<!-- 包含操作按钮。 -->
		<div style="margin: 10px 0;">
		  <!-- 新增按钮，点击触发handleAdd方法，并带有一个加号图标。 -->
		  <el-button type="primary" @click="handleAdd">新增<i class="el-icon-circle-plus-outline"></i></el-button>
		  <!-- 弹出确认框组件，用于批量删除。 -->
		  <el-popconfirm
		      class="ml-5"
		      confirm-button-text='确定'
		      cancel-button-text='我再想想'
		      icon="el-icon-info"
		      icon-color="red"
		      title="您确定批量删除这些数据吗？"
		      @confirm="delBatch"
		  >
		    <el-button type="danger" slot="reference">批量删除<i class="el-icon-remove-outline"></i></el-button>
		  </el-popconfirm>
		  <el-upload action="http://localhost:8181/user/import" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block;">
			<el-button type="primary" class="ml-5">导入<i class="el-icon-bottom"></i></el-button>
		  </el-upload>
		  <el-button type="primary" @click="exp" class="ml-5">导出<i class="el-icon-top"></i></el-button>
		</div>
		
		<!-- 表格组件，绑定数据为tableData，有边框和条纹效果，表头单元格的类名通过headerBg指定，当选择改变时触发handleSelectionChange方法。 -->
		<el-table :data="tableData" border stripe :header-cell-class-name="headerBg" @selection-change="handleSelectionChange">
		  <el-table-column type="selection" width="55">  <!-- 选择列。 -->
		  </el-table-column>  
		  <el-table-column prop="id" label="ID" width="140">  <!-- 显示 ID 的列。 -->
		  </el-table-column>
		  <el-table-column prop="username" label="用户名">  <!-- 显示用户名的列。 -->
		  </el-table-column>
		  <el-table-column prop="password" label="密码">  <!-- 显示密码的列。 -->
		  </el-table-column>
		  <el-table-column prop="email" label="邮箱">  <!-- 显示邮箱的列。 -->
		  </el-table-column>
		  <el-table-column prop="phone" label="电话">  <!-- 显示电话的列。 -->
		  </el-table-column>
		  <el-table-column label="操作" width="200" align="center">
		    <template slot-scope="scope">  <!-- 作用域插槽，用于操作列的内容。 -->
		      <!-- 编辑按钮，点击触发handleEdit方法，并带有一个编辑图标。 -->
			  <el-button type="success" @click="handleEdit(scope.row)">编辑<i class="el-icon-edit"></i></el-button>
		      <!-- 弹出确认框组件，用于单个删除,触发的方法为del(scope.row.id)，传入当前行的 ID。 -->
			  <el-popconfirm
		          class="ml-5"
		          confirm-button-text='确定'
		          cancel-button-text='我再想想'
		          icon="el-icon-info"
		          icon-color="red"
		          title="您确定删除吗？"
		          @confirm="del(scope.row.id)"
		      >
		      <el-button type="danger" slot="reference">删除<i class="el-icon-remove-outline"></i></el-button>
		      </el-popconfirm>
		    </template>
		  </el-table-column>
		</el-table>
		<!-- 包含分页组件 -->
		<div style="padding: 10px 0">
		  <!-- 分页组件，当页面大小改变时触发handleSizeChange方法，当当前页码改变时触发handleCurrentChange方法，
		  绑定当前页码为pageNum，可选的页面大小为[5, 10, 15, 20]，绑定当前页面大小为pageSize，
		  布局设置为显示总数、页面大小选择、上一页、页码、下一页和跳转输入框，绑定总数为total。 -->
		  <el-pagination
		      @size-change="handleSizeChange"
		      @current-change="handleCurrentChange"
		      :current-page="pageNum"
		      :page-sizes="[5, 10, 15, 20]"
		      :page-size="pageSize"
		      layout="total, sizes, prev, pager, next, jumper"
		      :total="total">
		  </el-pagination>
		</div>
		
		<!-- 对话框组件，用于新增或编辑用户信息。 -->
		<el-dialog title="用户信息" :visible.sync="dialogFormVisible" width="30%">
		  <!-- 表单组件，设置标签宽度为 80px，尺寸为 small。 -->
		  <el-form label-width="80px" size="small">
			<!-- 输入用户名的输入框，双向绑定到form.username，关闭自动完成。 -->
		    <el-form-item label="用户名">
		      <el-input v-model="form.username" autocomplete="off"></el-input>
		    </el-form-item>
			<el-form-item label="密码">
			  <el-input v-model="form.password" autocomplete="off"></el-input>
			</el-form-item>
		    <el-form-item label="邮箱">
		      <el-input v-model="form.email" autocomplete="off"></el-input>
		    </el-form-item>
		    <el-form-item label="电话">
		      <el-input v-model="form.phone" autocomplete="off"></el-input>
		    </el-form-item>
		  </el-form>
		  <!-- 对话框的底部区域。 -->
		  <div slot="footer" class="dialog-footer">
		    <el-button @click="dialogFormVisible = false">取 消</el-button>
		    <el-button type="primary" @click="save">确 定</el-button>
		  </div>
		</el-dialog>
	</div>
</template>

<script>
export default {
	name: "Header",  //为该组件指定一个名称。
	data(){
		return {
			tableData: [],  //存储表格数据的数组。
			total: 0,  //数据总数。
			pageNum: 1,  //当前页码。
			pageSize: 5,  //当前页面大小。
			username: "",  //用户名搜索输入框的值。
			email:"",  //邮箱搜索输入框的值。
			phone:"",  //电话搜索输入框的值。
			form:{},  //用于对话框中编辑或新增用户信息的对象。
			dialogFormVisible: false,  //控制对话框是否显示的布尔值。
			multipleSelection: [],  //存储表格中选中行的数组。
			headerBg:'headerBg'  //表头单元格的类名。
		}
	},
	// 在组件创建时调用load方法，加载数据。
	created() {
	  this.load()
	},
	methods: {
		// 通过this.request.get发送请求获取分页数据，
		// 请求参数包括当前页码、页面大小、用户名、邮箱和地址。请求成功后，将返回的数据更新到tableData和total。
		load() {
		    this.request.get("/user/page",{
		      params:{
		        pageNum:this.pageNum,
		        pageSize:this.pageSize,
		        username:this.username,
		        email:this.email,
		        address:this.address
		      }
		    }).then(res => {
		      console.log(res)
		      this.tableData = res.records
		      this.total = res.total
		    })
		  },
		  // 通过this.request.post发送请求保存或更新用户信息，请求成功后，显示成功消息，关闭对话框并重新加载数据。
		  save(){
		    this.request.post("/user/saveorupdate", this.form).then(res =>{
		      if(res){
		        this.$message.success("保存成功")
		        this.dialogFormVisible = false
		        this.load()
		      } else {
		        this.$message.error("保存失败")
		      }
		    })
		  },
		  // 显示对话框，清空form对象，用于新增用户。
		  handleAdd(){  //增
		    this.dialogFormVisible = true
		    this.form = {}
		  },
		  // 将选中的行数据赋值给form对象，显示对话框，用于编辑用户信息。
		  handleEdit(row){  //编辑
		    this.form = row
		    this.dialogFormVisible = true
		  },
		  // 通过this.request.delete发送请求删除单个用户，请求成功后，显示成功消息并重新加载数据。
		  del(id){
		    this.request.delete("/user/delete/" + id).then(res =>{
		      if(res) {
		        this.$message.success("删除成功")
		        this.load()
		      }else{
		        this.$message.error("删除失败")
		      }
		    })
		  },
		  // 当表格的选择改变时，更新multipleSelection数组。
		  handleSelectionChange(val){
		    console.log(val)
		    this.multipleSelection = val
		  },
		  // 批量删除用户，将选中行的 ID 提取出来发送请求，请求成功后，显示成功消息并重新加载数据。
		  delBatch(){
		    let ids = this.multipleSelection.map(v =>v.id)  //[{} ,{} ,{}] => [1 ,2 ,3]
		    this.request.post("/user/del/batch", ids).then(res =>{
		      if(res) {
		        this.$message.success("批量删除成功")
		        this.load()
		      }else {
		        this.$message.error("批量删除失败")
		      }
		    })
		  },
		  // 清空用户名、邮箱和地址搜索输入框的值，并重新加载数据。
		  reset(){
		    this.username = ""
		    this.email = ""
		    this.address = ""
		    this.load()
		  },
		  // 当页面大小改变时，更新pageSize并重新加载数据。
		  handleSizeChange(pageSize){
		    console.log(pageSize)
		    this.pageSize = pageSize
		    this.load()
		  },
		  // 当当前页码改变时，更新pageNum并重新加载数据。
		  handleCurrentChange(pageNum){
		    console.log(pageNum)
		    this.pageNum = pageNum
		    this.load()
		  },
		  exp() {
			  window.open("http://localhost:8181/user/export")
		  },
		  handleExcelImportSuccess(){
			  this.$message.success("文件导入成功")
			  this.load()
		  }
	}
}
</script>

<style>
/* 设置表头单元格的背景颜色为#eee，并设置为重要样式。 */
.headerBg{
  background: #eee!important;
}
</style>