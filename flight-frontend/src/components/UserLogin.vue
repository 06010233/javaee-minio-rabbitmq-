<template>
  <div class="login-layout-container">
    
    <!-- 1. 背景层 -->
    <div class="bg-placeholder"></div>
    
    <div class="video-background">
      <video 
        ref="bgVideo" 
        class="fullscreen-video"
        :src="videoSrc" 
        autoplay 
        muted 
        loop 
        playsinline
        style="object-fit: cover;"
      ></video>
    </div>

    <!-- 背景音乐 -->
    <audio ref="audioPlayer" loop style="display:none">
      <source src="/login_resources/bgm.mp3" type="audio/mp3">
    </audio>

    <!-- 2. 登录卡片 (UI层) -->
    <div class="main-card" v-if="showUI" @click.stop>
      
      <!-- 顶部功能栏 -->
      <div class="card-header">
         <div class="logo-container">
           <img src="/login_resources/logo.png" alt="MiHoYo" class="logo-img">
         </div>
         <!-- 点击关闭，返回首页 -->
         <div class="close-btn" @click="handleExit">
           <img src="/login_resources/close.png" alt="Close">
         </div>
      </div>

      <!-- 内容区 -->
      <div class="card-content">
        
        <!-- 登录视图 -->
        <template v-if="currentView === 'login'">
          <div class="role-tabs">
            <div class="tab-item" :class="{ active: loginType === 'user' }" @click="loginType = 'user'">乘客</div>
            <div class="tab-divider">/</div>
            <div class="tab-item" :class="{ active: loginType === 'admin' }" @click="loginType = 'admin'">管理员</div>
          </div>

          <el-form
            :model="activeFormData"
            :rules="loginType === 'user' ? userLoginRules : adminLoginRules"
            ref="loginFormRef"
            class="genshin-form"
            @submit.native.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input v-model="activeFormData.username" placeholder="请输入账号" class="genshin-input"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="activeFormData.password" type="password" placeholder="请输入密码" show-password class="genshin-input" @keyup.enter.native="handleLogin"></el-input>
            </el-form-item>
          </el-form>

          <div class="form-footer">
             <span class="text-btn" @click="currentView = 'register'">立即注册</span>
             <span class="text-btn forgot" @click="currentView = 'forgot'">忘记密码</span>
          </div>

          <button class="submit-btn" @click="handleLogin" :disabled="loading">
            {{ loading ? '正在进入...' : '进入系统' }}
          </button>
          
          <div class="social-login">
             <span>手机快捷登录</span>
             <span style="margin-left: 20px;">TapTap</span>
          </div>
        </template>

        <!-- 注册视图 -->
        <template v-else-if="currentView === 'register'">
           <div class="sub-view-title">注册新账号</div>
           <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="genshin-form scrollable">
              <el-form-item prop="name"><el-input v-model="registerForm.name" placeholder="真实姓名" class="genshin-input"></el-input></el-form-item>
              <el-form-item prop="idCard"><el-input v-model="registerForm.idCard" placeholder="身份证号" class="genshin-input"></el-input></el-form-item>
              <el-form-item prop="phone"><el-input v-model="registerForm.phone" placeholder="手机号" class="genshin-input"></el-input></el-form-item>
              <el-form-item prop="username"><el-input v-model="registerForm.username" placeholder="设置用户名" class="genshin-input"></el-input></el-form-item>
              <el-form-item prop="password"><el-input v-model="registerForm.password" type="password" placeholder="设置密码" class="genshin-input"></el-input></el-form-item>
           </el-form>
           <button class="submit-btn compact" @click="handleRegister">确认注册</button>
           <div class="back-link" @click="currentView = 'login'">返回登录</div>
        </template>

        <!-- 找回密码视图 (已修复：添加手机号，添加数据绑定) -->
        <template v-else-if="currentView === 'forgot'">
           <div class="sub-view-title">重置密码</div>
           <el-form :model="forgotForm" class="genshin-form">
              <el-form-item>
                <el-input v-model="forgotForm.username" placeholder="请输入用户名" class="genshin-input"></el-input>
              </el-form-item>
              <el-form-item>
                <el-input v-model="forgotForm.idCard" placeholder="请输入身份证号" class="genshin-input"></el-input>
              </el-form-item>
              <!-- 新增：手机号验证 -->
              <el-form-item>
                <el-input v-model="forgotForm.phone" placeholder="请输入预留手机号" class="genshin-input"></el-input>
              </el-form-item>
           </el-form>
           <button class="submit-btn compact" @click="handleResetNext" :disabled="loading">
             {{ loading ? '处理中...' : '重置密码' }}
           </button>
           <div class="back-link" @click="currentView = 'login'">返回登录</div>
        </template>

      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api/api';
import axios from 'axios'; // 引入axios用于调用重置接口

export default {
  name: 'LoginLayout',
  data() {
    return {
      videoSrc: '/login_resources/bg.mp4', 
      showUI: true,
      loading: false,
      currentView: 'login',
      loginType: 'user',
      audioPlayed: false,
      isRedirecting: false, 

      userLoginForm: { username: '', password: '' },
      adminLoginForm: { username: '', password: '' },
      
      // 修复：初始化 forgotForm 对象，包含手机号
      forgotForm: { username: '', idCard: '', phone: '' },

      registerForm: { name: '', idCard: '', phone: '', username: '', password: '' },
      
      userLoginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      adminLoginRules: {
        username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      registerRules: {
        name: [{ required: true, message: '必填', trigger: 'blur' }],
        username: [{ required: true, message: '必填', trigger: 'blur' }],
        password: [{ required: true, message: '必填', trigger: 'blur' }]
      }
    };
  },
  computed: {
    activeFormData() {
      return this.loginType === 'user' ? this.userLoginForm : this.adminLoginForm;
    }
  },
  mounted() {
    document.addEventListener('click', this.tryPlayMusic);
  },
  beforeDestroy() {
    document.removeEventListener('click', this.tryPlayMusic);
  },
  methods: {
    tryPlayMusic() {
      if (!this.audioPlayed && this.$refs.audioPlayer) {
        this.$refs.audioPlayer.play().then(() => {
          this.audioPlayed = true;
        }).catch(() => {});
      }
    },
    
    handleExit() {
      this.$router.push('/'); 
    },
    
    async handleLogin() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
             await this.$store.dispatch('login', this.activeFormData);
             this.playSuccessEffect(); 
          } catch (error) {
             console.error(error);
             const msg = error.response?.data?.message || error.message || '登录失败';
             this.$message.error(msg);
             this.loading = false;
          }
        }
      });
    },

    playSuccessEffect() {
        this.showUI = false;
        this.videoSrc = '/login_resources/bg2.mp4';
        
        this.$nextTick(() => {
            const video = this.$refs.bgVideo;
            if(video) {
                video.loop = false; 
                video.load();       
                video.play();       

                video.onended = () => {
                   this.performRedirect();
                };
                
                // 保底机制
                setTimeout(() => {
                    this.performRedirect();
                }, 15000);
            } else {
                this.performRedirect();
            }
        });
    },

    performRedirect() {
        if (this.isRedirecting) return;
        this.isRedirecting = true;
        
        if (this.loginType === 'admin') {
            this.$router.push('/admin/dashboard');
        } else {
            this.$router.push('/'); 
        }
    },

    handleRegister() {
        this.$refs.registerFormRef.validate(async (valid) => {
            if(valid) {
                try {
                    await api.register(this.registerForm);
                    this.$message.success('注册成功，请登录');
                    this.currentView = 'login';
                    this.userLoginForm.username = this.registerForm.username;
                } catch(e) {
                    this.$message.error('注册失败');
                }
            }
        })
    },

    // 修复：完整的重置密码逻辑
    async handleResetNext() {
      // 1. 验证三要素是否填写
      if(!this.forgotForm.username || !this.forgotForm.idCard || !this.forgotForm.phone) {
        this.$message.warning('请填写完整信息（用户名、身份证、手机号）');
        return;
      }
      
      try {
        this.loading = true;
        // 2. 调用后端接口
        // 如果你配置了全局 baseURL = '/api'，这里就不需要写 /api 了
const res = await axios.post('/system-users/reset-password', this.forgotForm);
        
        // 3. 成功回调
        this.$alert(res.data.message, '重置成功', {
          confirmButtonText: '去登录',
          callback: () => {
            this.currentView = 'login';
            this.userLoginForm.username = this.forgotForm.username;
            // 清空表单
            this.forgotForm = { username: '', idCard: '', phone: '' };
          }
        });
      } catch (error) {
        console.error(error);
        const msg = error.response?.data?.message || '重置失败，信息不匹配或系统错误';
        this.$message.error(msg);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
/* 样式部分保持不变 */
* { box-sizing: border-box; }
.login-layout-container { position: relative; width: 100vw; height: 100vh; overflow: hidden; font-family: "Microsoft YaHei", sans-serif; color: #333; }
.bg-placeholder { position: absolute; top:0; left:0; width:100%; height:100%; background-color: #f0f0f0; z-index: 0; }
.video-background { position: absolute; top:0; left:0; width:100%; height:100%; z-index: 1; overflow: hidden; }
.fullscreen-video { width: 100%; height: 100%; object-fit: cover; display: block; }
.main-card { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 640px; min-height: 480px; background: #fff; border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.15); z-index: 10; display: flex; flex-direction: column; }
.card-header { position: relative; height: 90px; display: flex; justify-content: center; align-items: center; }
.logo-img { height: 55px; object-fit: contain; margin-top: 10px; }
.close-btn { position: absolute; top: 20px; right: 20px; width: 30px; height: 30px; cursor: pointer; opacity: 0.7; transition: opacity 0.3s; }
.close-btn:hover { opacity: 1; }
.close-btn img { width: 100%; height: 100%; }
.card-content { padding: 10px 50px 40px 50px; flex: 1; }
.role-tabs { display: flex; justify-content: center; align-items: center; margin-bottom: 30px; font-size: 18px; color: #ccc; font-weight: bold; letter-spacing: 2px; }
.tab-item { cursor: pointer; transition: color 0.3s; padding: 0 10px; }
.tab-item.active { color: #333; }
.tab-divider { margin: 0 5px; font-weight: normal; }
.genshin-form { margin-bottom: 10px; }
.genshin-form >>> .el-form-item { margin-bottom: 24px; }
.genshin-form >>> .el-input__inner { height: 56px; line-height: 56px; background-color: #eef4fa; border: none; border-radius: 4px; color: #333; font-size: 16px; padding-left: 20px; transition: background-color 0.3s; }
.genshin-form >>> .el-input__inner:focus { background-color: #fff; box-shadow: 0 0 0 2px #dcdfe6; }
.genshin-form >>> .el-input__icon { font-size: 18px; line-height: 56px; }
.form-footer { display: flex; justify-content: space-between; margin-bottom: 25px; font-size: 14px; color: #999; }
.text-btn { cursor: pointer; transition: color 0.3s; }
.text-btn:hover { color: #409EFF; }
.forgot:hover { color: #E6A23C; }
.submit-btn { width: 100%; height: 60px; background-color: #393b40; color: #e3b785; font-size: 20px; font-weight: bold; border: none; border-radius: 4px; cursor: pointer; letter-spacing: 2px; transition: all 0.3s; box-shadow: 0 4px 10px rgba(57, 59, 64, 0.3); }
.submit-btn:hover { background-color: #4a4d54; transform: translateY(-1px); }
.submit-btn:active { transform: translateY(1px); }
.submit-btn:disabled { opacity: 0.7; cursor: not-allowed; }
.submit-btn.compact { height: 50px; font-size: 18px; margin-top: 10px; }
.social-login { margin-top: 30px; font-size: 14px; color: #666; font-weight: bold; }
.sub-view-title { text-align: center; font-size: 20px; margin-bottom: 20px; font-weight: bold; }
.back-link { text-align: center; margin-top: 20px; color: #999; cursor: pointer; font-size: 14px; }
.back-link:hover { text-decoration: underline; }
.scrollable { max-height: 350px; overflow-y: auto; padding-right: 5px; }
.scrollable::-webkit-scrollbar { width: 4px; }
.scrollable::-webkit-scrollbar-thumb { background: #ccc; border-radius: 2px; }
</style>