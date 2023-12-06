/**
 * This mixin uses the native browser prevent leave for unsaved changes.
 * To prevent leave when closing/refreshing the page if data has been changed, the component should set the value of `dirty` to true when changes occur.
 */
export default {
  data () {
    return {
      isNativePreventLeaveListening: false,
      dirty: false
    }
  },
  created () {
    this.initPreventLeave()
  },
  beforeDestroy () {
    this.destroyPreventLeave()
  },
  methods: {
    beforeUnloadHandler (e) {
      if (!this.dirty) {
        return undefined
      }
      const event = e || window.event
      this.invokeOnPreventLeave(event)
      event.preventDefault()
      event.returnValue = undefined
      return undefined
    },
    invokeOnPreventLeave (event) {
      if (typeof this.onPreventLeave === 'function') {
        this.onPreventLeave(event)
      }
    },
    initPreventLeave () {
      this.isNativePreventLeaveListening = true
      window.addEventListener('beforeunload', this.beforeUnloadHandler)
    },
    destroyPreventLeave () {
      window.removeEventListener('beforeunload', this.beforeUnloadHandler)
      this.isNativePreventLeaveListening = false
    }
  }
}
