<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
			if (uni.getStorageSync('user')) {
			  // 存在名为 'user' 的对象在 Storage 中
			  uni.switchTab({
			  	url: '/pages/index/index'
			  })
			} else {
			  // 不存在名为 'user' 的对象在 Storage 中
			}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>
