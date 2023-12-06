<script>
export default {
  name: 'CollapsibleActions',
  components: {
    Dropdown: () => import('@/components/utils/Dropdown.vue')
  },
  render () {
    /** @type {Array<import('vue').VNode>} */
    const actions = this.$scopedSlots.actions?.({ collapsed: false })
    const actionsNotCollapsed = actions?.filter((vnode) => vnode.tag !== undefined || vnode.text) ?? []
    if (actionsNotCollapsed.length === 1) {
      return <div> { actionsNotCollapsed[0] } </div>
    }
    if (actionsNotCollapsed.length > 1) {
      /** @type {Array<import('vue').VNode>} */
      const actions = this.$scopedSlots.actions?.({ collapsed: true })
      const actionsCollapsed = actions?.filter((vnode) => vnode.tag !== undefined || vnode.text) ?? []

      return <div>
        <dropdown
          scopedSlots={{
            'dropdown-trigger': ({ expanded }) => {
              const triggerCollapsed = this.$slots['trigger-collapsed'] || this.$scopedSlots['trigger-collapsed']()
              if (triggerCollapsed) {
                triggerCollapsed.forEach(vnode => {
                  vnode.data = vnode.data ?? {}
                  vnode.data.attrs = { 'aria-haspopup': 'menu', ...vnode.data.attrs, 'aria-expanded': String(expanded) }
                })
              }
              return triggerCollapsed
            },
            'dropdown-panel': () => (
              <ul
                class="dropdown-menu"
                role="menu"
              >
                { actionsCollapsed.map((vnode) => {
                  vnode.data = vnode.data ?? {}
                  vnode.data.staticClass = `${vnode.data?.staticClass ?? ''} dropdown-menu__link`
                  return <li
                    role="menuitem"
                    class="dropdown-menu__item"
                  >
                    { vnode }
                  </li>
                })}
              </ul>
            )
          }
          } />
      </div>
    }
    return null
  }
}
</script>
