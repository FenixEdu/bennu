import Vue from 'vue'
import Router from 'vue-router'
import store from '@/store'
import RefreshRoute from '@/utils/refresh-route'

// Navigation guards
import { buildLoginCallback, guardWithErrorHandling } from './guards'
// import { guardWithErrorHandling, buildLoginCallback } from './guards'

// Standard pages
import PageNotFoundPage from '@/pages/PageNotFoundPage'
import UnauthorizedPage from '@/pages/UnauthorizedPage'
import ErrorPage from '@/pages/ErrorPage'

// Utils
import { scrollOffset as DynamicFormScrollOffset, scrollBehavior as DynamicFormScrollBehavior } from '@/components/dynamic-form/utils/navigation'
// import { getMeta } from '@/router/utils'
import * as BennuAdminAPI from '@/api/bennu-admin'

// Pages
const DomainBrowserPage = () => import('@/pages/DomainBrowserPage.vue')
const DomainObjectPage = () => import('@/pages/DomainObjectPage.vue')
const DomainObjectRoleSetPage = () => import('@/pages/DomainObjectRoleSetPage.vue')
const DomainObjectSlotsTab = () => import('@/pages/DomainObjectSlotsTab.vue')
const DomainObjectRolesTab = () => import('@/pages/DomainObjectRolesTab.vue')
const DomainObjectRoleSetsTab = () => import('@/pages/DomainObjectRoleSetsTab.vue')
const DomainObjectEditPage = () => import('@/pages/DomainObjectEditPage.vue')

Vue.use(Router)

const ScrollBehavior = {
  savePosition ({ sameRoute = true, differentRoute = false }) {
    return function (to, from, savedPosition) {
      if (sameRoute && to.path === from.path) {
        return savedPosition
      }
      if (differentRoute && to.path !== from.path) {
        return savedPosition
      }
      return { x: 0, y: 0 }
    }
  },
  tabs () {
    return function (to, from, savedPosition) {
      const parentPathTo = to.fullPath.substring(0, to.fullPath.lastIndexOf('/'))
      const parentPathFrom = from.fullPath.substring(0, from.fullPath.lastIndexOf('/'))
      if (parentPathTo === parentPathFrom) {
        return savedPosition
      }
      return { x: 0, y: 0 }
    }
  }
}

const router = new Router({
  mode: 'history',
  base: `${process.env.CTX && !process.env.DEV ? '/' + process.env.CTX : ''}/fenixedu-bennu-admin`,
  routes: [
    {
      path: '/',
      name: 'LandingPage',
      redirect: { name: 'DomainBrowserPage' }
    },
    {
      path: '/domain-browser',
      name: 'DomainBrowserPage',
      component: DomainBrowserPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout'
      },
      props: (route) => ({
        query: route.query.q
      })
    },
    {
      path: '/domain-object/:domainObjectId',
      name: 'DomainObjectPage',
      component: DomainObjectPage,
      redirect: { name: 'DomainObjectSlotsTab' },
      meta: {
        layout: 'PageWithNavBarAndFooterLayout'
      },
      props: (route) => ({
        domainObject: route.meta.domainObject,
        metadata: route.meta.metadata
      }),
      beforeEnter: guardWithErrorHandling(async (to, from, next) => {
        const [domainObject, metadata] = await Promise.all([
          BennuAdminAPI.getDomainObject({ objectId: to.params.domainObjectId }),
          BennuAdminAPI.getDomainObjectMeta({ objectId: to.params.domainObjectId })
        ])
        to.meta.domainObject = domainObject
        to.meta.metadata = metadata
        next()
      }),
      // Something strange is happening here, the beforeEnter is not being called when changing tabs.
      // For now, we are calling the methods manually in each child route.
      children: [
        {
          path: 'slots',
          name: 'DomainObjectSlotsTab',
          component: DomainObjectSlotsTab,
          props: (route) => ({
            query: route.query.q,
            page: route.meta.page,
            perPage: route.meta.perPage,
            totalItems: route.meta.totalItems,
            domainObject: route.meta.domainObject,
            metadata: route.meta.metadata,
            domainObjectSlots: route.meta.domainObjectSlots
          }),
          meta: {
            scrollBehavior: ScrollBehavior.tabs(),
            beforeRouteLoad: async (to, from) => {
              to.meta.page = Number(to.query.page) || 1
              to.meta.perPage = 10

              const [domainObject, metadata, domainObjectSlots] = await Promise.all([
                BennuAdminAPI.getDomainObject({ objectId: to.params.domainObjectId }),
                BennuAdminAPI.getDomainObjectMeta({ objectId: to.params.domainObjectId }),
                BennuAdminAPI.listDomainObjectSlots({
                  objectId: to.params.domainObjectId,
                  query: to.query.q,
                  page: to.meta.page,
                  perPage: to.meta.perPage
                })
              ])

              to.meta.domainObject = domainObject
              to.meta.metadata = metadata
              to.meta.totalItems = domainObjectSlots.totalItems
              to.meta.domainObjectSlots = domainObjectSlots.items
            }
          },
          beforeEnter: guardWithErrorHandling(async (to, from, next) => {
            await to.meta.beforeRouteLoad(to, from)
            next()
          })
        },
        {
          path: 'roles',
          name: 'DomainObjectRolesTab',
          component: DomainObjectRolesTab,
          props: (route) => ({
            query: route.query.q,
            page: route.meta.page,
            perPage: route.meta.perPage,
            totalItems: route.meta.totalItems,
            metadata: route.meta.metadata,
            domainObject: route.meta.domainObject,
            domainObjectRoles: route.meta.domainObjectRoles
          }),
          meta: {
            scrollBehavior: ScrollBehavior.tabs(),
            beforeRouteLoad: async (to, from) => {
              to.meta.page = Number(to.query.page) || 1
              to.meta.perPage = 10

              const [domainObject, metadata, domainObjectRoles] = await Promise.all([
                BennuAdminAPI.getDomainObject({ objectId: to.params.domainObjectId }),
                BennuAdminAPI.getDomainObjectMeta({ objectId: to.params.domainObjectId }),
                BennuAdminAPI.listDomainObjectRoles({
                  objectId: to.params.domainObjectId,
                  query: to.query.q,
                  page: to.meta.page,
                  perPage: to.meta.perPage
                })
              ])

              // Load slots
              await Promise.all(
                domainObjectRoles.items.map(async (relation) => {
                  if (relation.objectId) {
                    const slots = await BennuAdminAPI.listDomainObjectSlots({
                      objectId: relation.objectId,
                      query: to.query.q,
                      page: 1,
                      perPage: 5
                    })
                    relation.count = {
                      ...relation.count,
                      slots: slots.totalItems
                    }
                    relation.slots = slots.items
                  }
                })
              )

              to.meta.domainObject = domainObject
              to.meta.metadata = metadata
              to.meta.totalItems = domainObjectRoles.totalItems
              to.meta.domainObjectRoles = domainObjectRoles.items
            }
          },
          beforeEnter: guardWithErrorHandling(async (to, from, next) => {
            await to.meta.beforeRouteLoad(to, from)
            next()
          })
        },
        {
          path: 'role-sets',
          name: 'DomainObjectRoleSetsTab',
          component: DomainObjectRoleSetsTab,
          props: (route) => ({
            query: route.query.q,
            page: route.meta.page,
            perPage: route.meta.perPage,
            totalItems: route.meta.totalItems,
            domainObject: route.meta.domainObject,
            metadata: route.meta.metadata,
            domainObjectRoleSets: route.meta.domainObjectRoleSets
          }),
          meta: {
            scrollBehavior: ScrollBehavior.tabs(),
            beforeRouteLoad: async (to, from) => {
              to.meta.page = Number(to.query.page) || 1
              to.meta.perPage = 10

              const [domainObject, metadata, domainObjectRoleSets] = await Promise.all([
                BennuAdminAPI.getDomainObject({ objectId: to.params.domainObjectId }),
                BennuAdminAPI.getDomainObjectMeta({ objectId: to.params.domainObjectId }),
                BennuAdminAPI.listDomainObjectRoleSets({
                  objectId: to.params.domainObjectId,
                  query: to.query.q,
                  page: to.meta.page,
                  perPage: to.meta.perPage
                })
              ])

              to.meta.domainObject = domainObject
              to.meta.metadata = metadata
              to.meta.totalItems = domainObjectRoleSets.totalItems
              to.meta.domainObjectRoleSets = domainObjectRoleSets.items
            }
          },
          beforeEnter: guardWithErrorHandling(async (to, from, next) => {
            await to.meta.beforeRouteLoad(to, from)
            next()
          })
        }
      ]
    },
    {
      path: '/domain-browser/:domainObjectId/role-set/:roleSetName',
      name: 'DomainObjectRoleSetPage',
      component: DomainObjectRoleSetPage,
      props: (route) => ({
        query: route.query.q,
        page: route.meta.page,
        perPage: route.meta.perPage,
        totalItems: route.meta.totalItems,
        domainObject: route.meta.domainObject,
        relationSet: route.meta.relationSet
      }),
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        beforeRouteLoad: async (to, from) => {
          to.meta.page = Number(to.query.page) || 1
          to.meta.perPage = 10

          const [domainObject, relationSet] = await Promise.all([
            BennuAdminAPI.getDomainObject({
              objectId: to.params.domainObjectId
            }),
            BennuAdminAPI.getDomainObjectRoleSet({
              objectId: to.params.domainObjectId,
              roleSetName: to.params.roleSetName,
              query: to.query.q,
              page: to.meta.page,
              perPage: to.meta.perPage
            })
          ])

          // Load slots
          await Promise.all(
            relationSet.items.map(async (relation) => {
              if (relation.objectId) {
                const slots = await BennuAdminAPI.listDomainObjectSlots({
                  objectId: relation.objectId,
                  query: to.query.q,
                  page: 1,
                  perPage: 5
                })
                relation.slots = slots.items
              }
            })
          )

          to.meta.domainObject = domainObject
          to.meta.totalItems = relationSet.totalItems
          to.meta.relationSet = relationSet.items
        }
      },
      beforeEnter: guardWithErrorHandling(async (to, from, next) => {
        await to.meta.beforeRouteLoad(to, from)
        next()
      })
    },
    {
      path: '/domain-object/:domainObjectId/edit',
      name: 'DomainObjectEditPage',
      component: DomainObjectEditPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        scrollBehavior: DynamicFormScrollBehavior,
        scrollOffset: DynamicFormScrollOffset
      },
      props: (route) => ({
        domainObject: route.meta.domainObject,
        domainObjectForm: route.meta.domainObjectForm
      }),
      beforeEnter: guardWithErrorHandling(async (to, from, next) => {
        const [domainObject, domainObjectForm] = await Promise.all([
          BennuAdminAPI.getDomainObject({ objectId: to.params.domainObjectId }),
          BennuAdminAPI.getDomainObjectForm({ objectId: to.params.domainObjectId })
        ])
        to.meta.domainObject = domainObject
        to.meta.domainObjectForm = domainObjectForm
        console.log({ domainObjectForm })
        next()
      })
    },
    {
      path: '/login',
      name: 'LoginPage',
      meta: {
        public: true
      },
      beforeEnter (to, from, next) {
        const callback = buildLoginCallback()
        window.location.href = `${process.env.VUE_APP_CTX ?? ''}/fenixedu-connect?callback=${callback}`
      }
    },
    {
      path: '/error',
      name: 'ErrorPage',
      component: ErrorPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        public: true
      },
      props: (route) => ({ error: route.params.error }),
      beforeEnter: (to, from, next) => {
        if (!!to.params.error && to.params.error instanceof Error) {
          next()
        } else {
          next({ path: '/' })
        }
      }
    },
    {
      path: '/unauthorized',
      name: 'UnauthorizedPage',
      component: UnauthorizedPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        public: true
      },
      props: (route) => ({
        errorKey: route.params.key,
        errorMessage: route.params.message
      })
    },
    {
      path: '*',
      name: 'PageNotFoundPage',
      component: PageNotFoundPage,
      meta: {
        layout: 'PageWithNavBarAndFooterLayout',
        public: true
      }
    },
    {
      path: '/logout',
      name: 'Logout'
    }
  ],
  scrollBehavior (to, from, savedPosition) {
    if (to.meta.scrollBehavior instanceof Function) {
      return to.meta.scrollBehavior.call(this, to, from, savedPosition)
    }
    return savedPosition ?? { x: 0, y: 0 }
  }
})

Vue.use(RefreshRoute, { router })

router.beforeEach(async (to, from, next) => {
  // Clear non-persisted notifications
  if (store.state.topMessage.active && !store.state.topMessage.persist) {
    Vue.prototype.$notification.clear()
  }

  if (to.path === from.path) {
    next()
    return
  }

  await Vue.prototype.$progress.set(10)
  if (to.path === '/logout') {
    await router.app.$auth.logout()
    return
  }
  const isLogged = await router.app.$auth.isLogged()
  await Vue.prototype.$progress.set(60)
  if (isLogged || to.meta.public) {
    next()
  } else {
    next({ name: 'LoginPage' })
  }
})

router.afterEach(async (to, from) => {
  if (to.path !== from.path) {
    await Vue.prototype.$progress.complete()
  }
})

router.onError((error) => {
  Vue.config.errorHandler(error, router.app, null)
})

export default router
