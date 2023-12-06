/**
 * @param {import('vue-router').default} router
 */
export function registerRefreshRoute (router) {
  router.addRoutes([
    {
      path: '/refresh',
      name: 'RefreshRoute',
      beforeEnter (to, from, next) {
        const metas = from.matched.filter(r => r.meta.layout).map(r => r.meta.layout)
        const layout = metas.length > 0 ? metas[metas.length - 1] : undefined
        to.meta.layout = layout
        next()
      },
      component: {
        render () {
          return <div class="page" style="text-align: center;">
            <p>{ this.$t('header') }</p>
          </div>
        },
        i18n: {
          messages: {
            pt: {
              header: 'A recarregar o conteúdo da página…'
            },
            en: {
              header: 'Refreshing page content…'
            }
          }
        },
        beforeRouteEnter (to, from, next) {
          next(vm => {
            vm.$router.replace(from.fullPath)
          })
        }
      }
    }
  ])
}

/**
 * @this import('vue').default
 */
export function refreshRoute () {
  return this.$router.push({ name: 'RefreshRoute', replace: true })
}

export default {
  /**
   * @param {import('vue').VueConstructor<import('vue').default>} Vue
   * @param {Object} param1
   * @param {import('vue-router').default} param1.router
   */
  install (Vue, { router }) {
    Vue.mixin({
      methods: {
        refreshRoute () {
          return refreshRoute.call(this)
        }
      }
    })

    registerRefreshRoute(router)
  }
}
