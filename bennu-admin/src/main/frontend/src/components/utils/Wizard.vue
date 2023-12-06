<template>
  <div
    ref="wizard"
    tabindex="-1"
    class="u-a11y-focusable"
  >
    <slot
      :component="currentStep.component"
      :push="push"
      :back="back"
    />
  </div>
</template>
<script>
export default {
  props: {
    firstStep: {
      type: String,
      required: true
    },
    steps: {
      type: Array,
      default: undefined
    }
  },
  data () {
    const step = { component: this.firstStep, x: 0, y: 0 }
    return {
      currentStep: step,
      history: [step]
    }
  },
  watch: {
    currentStep: {
      immediate: true,
      handler (newStep) {
        // reset focus and scroll to top on step change
        this.$nextTick(() => {
          this.$refs.wizard.focus()
          if (newStep.x !== undefined && newStep.y !== undefined) {
            // FIXME: when the wizard is inside the modal, the page scrolls to the top before the modal opens
            window.scrollTo(newStep.x, newStep.y)
          } else {
            this.$refs.wizard.scrollIntoView(true)
          }
        })
        this.$emit('step', newStep.component)
      }
    }
  },
  methods: {
    back (component) {
      while (this.history.length > 0) {
        const step = this.history.pop()
        if (component && step.component !== component) {
          continue
        } else if (component && step.component === component) {
          this.history.push(step)
          this.currentStep = step
          return true
        } else if (this.history.length > 0) {
          this.currentStep = this.history[this.history.length - 1]
          return true
        }
      }
      return false
    },
    push (component) {
      if (component) {
        this.savePosition()
        this.currentStep = { component }
        this.history.push(this.currentStep)
      } else if (this.steps) {
        const nextIndex = this.steps.indexOf(this.currentStep.component) + 1
        if (nextIndex < this.steps.length) {
          this.savePosition()
          this.currentStep = { component: this.steps[nextIndex] }
          this.history.push(this.currentStep)
        }
      }
    },
    savePosition () {
      const step = this.history.pop()
      if (step) {
        this.history.push({ ...step, x: window.scrollX ?? 0, y: window.scrollY ?? 0 })
      }
    }
  }
}
</script>
