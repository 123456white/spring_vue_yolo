<template>
	<!-- 创建一个菜单组件，设置了初始展开的菜单项索引为['1', '3']，最小高度为视口高度，隐藏溢出内容，
	背景颜色为特定的深蓝色，文本颜色为白色，激活状态的文本颜色为金黄色，关闭折叠过渡效果，
	根据isCollapse属性控制菜单的折叠状态，开启路由模式，并在菜单选项被选中时触发handleSelect方法。 -->
	<el-menu :default-openeds="['1', '3']" style="min-height: 100vh;overflow: hidden"
	         background-color="rgb(48,65,86)"
	         text-color="#fff"
	         active-text-color="#ffd04b"
	         :collapse-transition="false"
	         :collapse="isCollapse"
			 router
			 @select="handleSelect"
			 >
	  <!-- 一个包含标题和图标的容器，设置了固定高度为 60px 和行高为 60px，文本居中对齐。 -->
	  <div style="height: 60px; line-height: 60px; text-align: center;">
		<!-- 一个图标图片，设置了宽度为 20px，相对定位并向上偏移 5px，右侧外边距为 5px。 -->
	    <img src="../assets/title.jpg" alt="" style="width: 20px;
					  position: relative; top: 5px; margin-right: 5px;"/>
		<!-- 一个加粗的文本，颜色为白色，根据logoTextShow属性决定是否显示，用于显示系统名称。 -->
	    <b style="color: aliceblue;" v-show="logoTextShow">益农慧眼系统</b>
	  </div>
	  
	  <!-- 一个菜单项，索引为 “/”。 -->
	  <el-menu-item index="/">
		    <!-- 一个自定义图标。 -->
			<i class="el-icon-s-custom"></i>
			<!-- 菜单项的标题为 “主页”。 -->
			<span slot="title">主页</span>
	  </el-menu-item>
	  <el-submenu index="2">
	    <template slot="title">
	      <i class="el-icon-menu"></i>
	      <span slot="title">系统管理</span>
	    </template>
		<el-menu-item index="/user">
			<i class="el-icon-s-custom"></i>
			<span slot="title">用户管理</span>
		</el-menu-item>
		<el-menu-item index="/data-center">
		    <i class="el-icon-s-custom"></i>
		    <span slot="title">数据中心</span>
		</el-menu-item>
	  </el-submenu>>
	</el-menu>
</template>

<script>
export default {
	name: "Aside",
	props:{
		isCollapse: Boolean,  //接收一个布尔类型的属性，用于控制菜单的折叠状态。
		logoTextShow: Boolean  //接收一个布尔类型的属性，用于控制是否显示系统名称。
	},
	methods: {
		// 当菜单选项被选中时触发的方法，目前只是打印了选中的索引，但可以根据实际需求进行扩展，比如进行路由跳转等操作。
		handleSelect(index) {
			// console.log(index)
			// //console.log(this.$router.options.routes)
			// alert(this.$route.name)
		}
	}
}
</script>

<style scoped>
</style>