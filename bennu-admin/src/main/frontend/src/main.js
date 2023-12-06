import Vue from 'vue'
import App from '@/App'
import store from '@/store'
import router from '@/router'

import i18n, { setLocale } from '@/i18n'

import dayjs from 'dayjs'
import LocalizedFormat from 'dayjs/plugin/localizedFormat'
import { parseLocaleString } from '@/mixins/TranslateApiString'
dayjs.extend(LocalizedFormat)

Vue.mixin({
  methods: { setLocale }
})

Vue.prototype.$auth = {
  async isLogged () {
    try {
      await store.dispatch('fetchProfile')
      return true
    } catch (err) {
      return false
    }
  },
  async logout () {
    if (process.env.NODE_ENV === 'production') {
      window.location.href = `${process.env.VUE_APP_CTX ?? ''}/logout`
    } else {
      console.warn('Logout disabled in non-production environments')
    }
  }
}

Vue.prototype.$notification = {
  send: function ({ key, message, params, type, persist }) {
    store.dispatch('setTopMessage', {
      active: true,
      msg: key
        ? {
            pt: i18n.t(key, 'pt', params || {}),
            en: i18n.t(key, 'en', params || {})
          }
        : parseLocaleString(i18n.availableLocales, message),
      dismiss: true,
      type,
      persist: persist || false
    })
  },
  clear: function () {
    store.dispatch('setTopMessage', { active: false, msg: { pt: '', en: '' }, dismiss: false, type: '', persist: false })
  }
}

Vue.prototype.$progress = {
  set: function (value) {
    return store.dispatch('setProgressBar', value)
  },
  complete: function () {
    return store.dispatch('completeProgressBar')
  }
}

window.addEventListener('offline', () => {
  Vue.prototype.$notification.send({ key: 'message.error.noNetwork', type: 'warn', persist: true })
}, false)

window.addEventListener('online', () => {
  if (store.state.topMessage.active && store.state.topMessage.msg[i18n.locale] === i18n.t('message.error.noNetwork')) {
    Vue.prototype.$notification.clear()
  }
}, false)

Vue.config.errorHandler = (error, vm, info) => {
  if (error.isAxiosError) {
    if (error.response.status === 401) {
      vm.$router.push({ name: 'LoginPage' })
    } else if (error.response.status === 403) {
      vm.$router.push({ name: 'UnauthorizedPage', params: { key: error.response.data?.key, message: error.response.data?.message } })
    } else {
      vm.$router.push({ name: 'ErrorPage', params: { error } })
    }
  } else {
    console.error(error)
  }
}

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render (createElement) {
    return createElement(App, {})
  }
})
