<template>
	<!-- 创建一个具有固定行高为 60px 且使用 flex 布局的容器。 -->
	<div style=" line-height: 60px;display: flex">
		<!-- 占据剩余空间的子容器。 -->
		<div style="flex: 1;">
		  <!-- 一个带有动态类名的元素，可通过点击触发handleCollapse方法，用于控制侧边栏的收缩或展开，设置了鼠标指针为手型和字体大小为 18px。 -->
		  <span :class="collapseBtnClass" style="cursor: pointer; font-size: 18px " @click="handleCollapse"></span>
		  
		  <!-- 面包屑导航组件，分隔符为 “/”，设置了左侧外边距为 10px。 -->
		  <el-breadcrumb separator="/" style="display: inline-block; margin-left: 10px;">
			  <!-- 指向首页的面包屑项。 -->
		    <el-breadcrumb-item :to="'/'" >首页</el-breadcrumb-item>
			<!-- 动态显示当前路径名称的面包屑项。 -->
			<el-breadcrumb-item> {{ currentPathName }}</el-breadcrumb-item>
		  </el-breadcrumb>
		  
		</div>
		<!-- 下拉菜单组件，设置宽度为 70px 和鼠标指针为手型。 -->
		<el-dropdown style="width: 70px; cursor: pointer">
		  <!-- 显示用户名称和下拉箭头图标。 -->
		  <span>王小虎</span><i class="el-icon-arrow-down" style="margin-right: 5px"></i>
		  <!-- 下拉菜单内容区域，设置宽度为 100px 和文本居中。 -->
		  <el-dropdown-menu slot="dropdown" style="width: 100px; text-align:center;">
			  <!-- 设置字体大小为 14px 和内边距为上下 5px。 -->
			<el-dropdown-item style="font-size: 14px; padding: 5px 0">个人信息</el-dropdown-item>
			<el-dropdown-item style="font-size: 14px; padding: 5px 0">
				<router-link to="/login" style="text-decoration: none;">退出登录</router-link>
			</el-dropdown-item>
		  </el-dropdown-menu>
		</el-dropdown>
	</div>
</template>

<script>
export default {
	name: "Header",
	props:{
		collapseBtnClass: String,  //接收一个字符串类型的属性，用于动态设置收缩按钮的类名。
		// collapse: Boolean
		collapseEvent: Function  //接收一个函数用于触发收缩或展开事件
	},
	computed: {
		currentPathName () {
			return this.$store.state.currentPathName;  //需要监听的数据，计算属性，通过this.$store.state.currentPathName获取当前路径名称。
		}
	},
	watch:{
		//监听currentPathName的变化，当变化时打印新值。
		currentPathName (newVal, oldVal) {
			console.log(newVal)
		}
	},
	methods: {
		//点击收缩按钮时触发的方法，调用父组件传递过来的collapseEvent函数，实现侧边栏的收缩或展开控制。
		handleCollapse() {
		      // 触发父组件传递过来的函数
		      this.collapseEvent();
		}
	}
}
</script>

<style scoped>
</style>