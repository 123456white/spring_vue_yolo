
<template>
  <!-- 创建一个具有最小高度为视口高度（100vh）的容器。 -->
  <div style="min-height: 100vh">
	<!-- 使用 Element UI 的容器组件，同样设置最小高度为视口高度。 -->
    <el-container style="min-height: 100vh;">
      <!-- 侧边栏部分，宽度由sideWidth变量动态控制，设置了背景颜色和阴影效果。内部引入了<Aside>组件，并传递了isCollapse和logoTextShow两个属性。 -->
	  <el-aside :width="sideWidth + 'px'" style="background-color: rgb(238, 241, 246);height: 100%; box-shadow: 2px 0 6px rgb(0 21 41 / 35%)">
        <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" />
      </el-aside>
	  
      <el-container>
		<!-- 顶部导航栏，设置了底部边框。内部引入了<Header>组件，并传递了collapseBtnClass和collapseEvent两个属性。 -->
        <el-header style=" border-bottom: 1px solid #ccc; ">
          <Header :collapseBtnClass="collapseBtnClass" 	:collapseEvent="collapse" />
        </el-header>

        <el-main>
			<!--表示当前页面的子路由会在<router-view />里面展示-->
			<router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<script>
// 导入自定义的侧边栏组件和头部组件。
import Aside from "@/components/Aside";
import Header from "@/components/Header";

export default {
  name: 'Manage',  //为该组件指定一个名称。
  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',  //导航栏收缩按钮
      isCollapse:false ,  //默认是展开的
      sideWidth:200  ,  //展开默认大小200
      logoTextShow:true  //表示是否显示 logo 文本。
    }
  },
  // 注册引入的侧边栏和头部组件。
  components: {
  	Aside,
	Header
  },
  methods:{
    collapse(){  //点击收缩按钮触发
      this.isCollapse = !this.isCollapse  //切换isCollapse状态
      if(this.isCollapse){  //收缩状态
        this.sideWidth = 64  //将侧边栏宽度设置为 64px
        this.collapseBtnClass = 'el-icon-s-unfold'  //更改为展开按钮的类名。
        this.logoTextShow = false  //不显示 logo 文本
      }else{  //展开状态
        this.sideWidth = 200  //恢复侧边栏宽度为 200px。
        this.collapseBtnClass = 'el-icon-s-fold'  //恢复收缩按钮的类名。
        this.logoTextShow = true  //显示 logo 文本。
      }
    }
  }
}
</script>

<style>
/* 为顶部导航栏设置背景颜色、文字颜色和行高。 */
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}
/* 为侧边栏设置文字颜色。 */
.el-aside {
  color: #333;
}
</style>