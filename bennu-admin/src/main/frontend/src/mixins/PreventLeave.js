import PreventLeaveModal from '@/components/PreventLeaveModal.vue'

/**
 * This mixin uses the PreventLeaveModal component to prevent leaving the
 * route/component with unsaved changes.
 * To prevent leave when data has been changed, the page/component should set
 * the value of `dirty` to true when changes occurr.
 * The page/component should then call `confirmLeave` when the user attempts to
 * leave the page. If there are unsaved changes, the PreventLeaveModal will be
 * opened so that the user may confirm whether to leave or to stay on the page.
 *
 * How to use the PreventLeaveModal component:
 *```html
 * <prevent-leave-modal
 *   v-bind="preventLeave__attrs"
 *   v-on="preventLeave__listeners"
 * />
 * ```
 */
export default {
  components: {
    PreventLeaveModal
  },
  data () {
    return {
      dirty: false,
      preventLeave__isModalOpen: false,
      preventLeave__onConfirmLeave: undefined
    }
  },
  computed: {
    preventLeave__attrs () {
      return { value: this.preventLeave__isModalOpen }
    },
    preventLeave__listeners () {
      return {
        input: (val) => {
          this.preventLeave__isModalOpen = val
        },
        leave: () => {
          this.preventLeave__onConfirmLeave()
          this.$nextTick(() => {
            this.preventLeave__isModalOpen = false
          })
        }
      }
    }
  },
  methods: {
    /**
     * @param {Function} onConfirmLeave A function that repeats the original
     * "leaving" action when the user confirms that they want to leave the page
     * without saving changes. This is needed because this confirmation modal is
     * not blocking (unlike window.confirm, which is blocking) so this method
     * needs to save what the original "leaving" action was.
     * @returns `true` if there weren't any unsaved changes (the modal will not
     * be triggered, navigation can proceed), and `false` otherwise.
     */
    confirmLeave (onConfirmLeave) {
      if (this.dirty && !this.preventLeave__isModalOpen) {
        this.preventLeave__onConfirmLeave = onConfirmLeave
        this.preventLeave__isModalOpen = true
        return false
      } else {
        this.preventLeave__onConfirmLeave = undefined
        this.preventLeave__isModalOpen = false
        return true
      }
    }
  }
}
