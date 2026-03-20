<template>
  <view class="container">
    <!-- 左边选项区域 -->
    <view class="left-side">
      <view class="option" v-for="(item, index) in leftOptions" :key="index" @click="changeData(index)">
        {{ item }}
      </view>
    </view>

    <!-- 右边数据展示区域 -->
    <view class="right-side">
      <view v-if="selectedOption === 0">
        <!-- 第一个选项对应的数据 -->
		<uni-list>
			<uni-list-item v-for="item in data1" :key="item.id" :title="item.name" :thumb="item.img" thumb-size="base" showArrow  clickable @click="handleItemClick(item)"></uni-list-item>
		</uni-list>
      </view>
      <view v-if="selectedOption === 1">
        <!-- 第二个选项对应的数据 -->
        <uni-list>
        	<uni-list-item v-for="item in data2" :key="item.id" :title="item.name" :thumb="item.img" thumb-size="base" showArrow  clickable @click="handleItemClick(item)"></uni-list-item>
        </uni-list>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      leftOptions: ['推荐', '所有'],
      selectedOption: 0,
      // 右边数据展示区域的数据
      // data1: '选项1对应的数据',
	  data1: [],
	  data2: [],
	  id: '',
	  time: '',
	  
    };
  },
  onLoad(query) {
	this.id = uni.getStorageSync('user').id;
	this.time = query.time;
  	this.request({url: '/food/recommend?userId=' + this.id  + '&time=' + this.time, method: 'GET'}).then(res => {
  		if(res.code === '200'){
  			console.log(res.data);
			this.data1 = res.data;
  		}else{
  			
  		}
  	});
	this.request({url: '/food/all?userId=' + this.id  + '&time=' + this.time, method: 'GET'}).then(res => {
		if(res.code === '200'){
			console.log(res.data);
			this.data2 = res.data;
		}else{
			
		}
	});
  },
  methods: {
    // 切换右边数据展示区域的数据
    changeData(index) {
      this.selectedOption = index;
    },
	handleItemClick(item){
		uni.navigateTo({
			url: '/pages/food/detail?id=' + item.id
		})
	}
	
  },
};
</script>

<style>
.container {
  width: 100%;
}

.left-side {
  width: 10%;
  float: left;
}

.right-side {
  width: 90%;
  float: left;
}
</style>