<template>
  <div class="layout-sidebar">
    <div class="layout-sidebar-two-columns-container">
      <div
        v-if="pages.length > 0"
        class="layout-sidebar__sidebar layout-sidebar__sidebar--sm"
      >
        <div class="card dynamic-form__sidebar">
          <nav
            class="card-section card-section--header dynamic-form__navigation"
            aria-labelledby="form-navigation"
          >
            <p
              id="form-navigation"
              class="sr-only"
            >
              {{ $t("dynamic-form.navigation.aria-label") }}
            </p>
            <ul>
              <li
                v-for="(page, pageIndex) in pages"
                :key="`nav-${pageIndex}`"
                class="dynamic-form__navigation-page"
              >
                <p class="card-section__title h5--ssp">
                  {{ translate(page.title) }}
                  <span class="sr-only">({{ $t('dynamic-form.progress', { current: pageIndex + 1, total: pages.length }) }})</span>
                </p>
                <ul class="block-list">
                  <li
                    v-if="pageIndex === 0 && !!$slots['header-card-title'] && !!$slots['header-card-content']"
                    :aria-current="currentPage === 0 && currentSection == -1 ? 'true' : null"
                  >
                    <router-link
                      :to="getSectionLocation(0, -1)"
                      :class="{ 'u-active-link': currentPage === 0 && currentSection == -1 }"
                      @click.native="currentPage === -1 && focusOnTargetSection()"
                    >
                      <slot name="header-card-title" />
                    </router-link>
                  </li>
                  <li
                    v-for="(section, sectionIndex) in page.sections"
                    :key="getSectionAnchor(pageIndex, sectionIndex)"
                    :aria-current="currentPage === pageIndex && currentSection == sectionIndex ? 'true' : null"
                  >
                    <router-link
                      :to="getSectionLocation(pageIndex, sectionIndex)"
                      :class="{ 'u-active-link': currentPage === pageIndex && currentSection == sectionIndex }"
                      @click.native="currentPage === pageIndex && focusOnTargetSection()"
                    >
                      {{ translate(section.title) }}
                    </router-link>
                  </li>
                </ul>
              </li>
            </ul>
          </nav>
          <div class="card-section">
            <div class="btn-group--vertical">
              <button
                class="btn--light btn--full"
                :disabled="!hasPreviousStep && currentPage < 1"
                @click.prevent="previousStep"
              >
                {{ $t('actions.previous') }}
              </button>
              <button
                v-if="currentPage < pages.length - 1"
                class="btn--primary btn--full"
                @click.prevent="nextStep"
              >
                {{ $t('actions.next') }}
              </button>
              <button
                v-else
                type="submit"
                class="btn--primary btn--full"
                @click.prevent="nextStep"
              >
                <slot name="submit-action-text">
                  {{ $t('dynamic-form.actions.review') }}
                </slot>
              </button>
            </div>
          </div>
        </div>
      </div>
      <div class="layout-sidebar__main">
        <warning-box
          v-if="errorsSnapshot !== undefined"
          ref="form-errors"
          class="dynamic-form__errors"
          type="danger"
          role="alert"
          aria-labelledby="form-errors"
        >
          <template #badge>
            <span id="form-errors">{{ $t('dynamic-form.global-errors.header') }}</span>
          </template>
          <template #message>
            <p class="dynamic-form__errors-description">
              {{ $t('dynamic-form.global-errors.description') }}
            </p>
            <template v-for="(page, pageIndex) in errorsSnapshot.snapshot">
              <template v-for="(section, sectionIndex) in page">
                <template v-for="(snapshot, fieldName) in section">
                  <component
                    :is="ErrorFields[snapshot.field.type]"
                    :key="`${pageIndex}-${sectionIndex}-${fieldName}`"
                    :snapshot="snapshot"
                  />
                </template>
              </template>
            </template>
          </template>
        </warning-box>
        <transition
          appear
          name="fade"
          mode="out-in"
          @after-enter="onPageChanged"
        >
          <div :key="currentPage">
            <template v-if="pages.length > 0">
              <h2
                class="h4"
                style="margin-bottom: 2rem;"
              >
                {{ translate(pages[currentPage].title) }} <span class="sr-only">({{ $t('dynamic-form.progress', { current: currentPage + 1, total: pages.length }) }})</span>
              </h2>
              <section
                v-if="currentPage === 0 && !!$slots['header-card-title'] && !!$slots['header-card-content']"
                :id="getSectionAnchor(0, -1)"
                ref="header-section"
                tabindex="-1"
                class="card dynamic-form__section"
              >
                <div class="card-row">
                  <div class="card-row__text">
                    <h3 class="card-row__title h4">
                      <slot name="header-card-title" />
                    </h3>
                  </div>
                </div>
                <slot name="header-card-content" />
              </section>
              <section
                v-for="(section, sectionIndex) in sections"
                :id="getSectionAnchor(currentPage, sectionIndex)"
                :key="getSectionAnchor(currentPage, sectionIndex)"
                ref="sections"
                tabindex="-1"
                class="card dynamic-form__section"
              >
                <div class="card-row">
                  <div class="card-row__text">
                    <h3 class="h4 card-row__title">
                      {{ translate(section.title) }}
                    </h3>
                  </div>
                </div>
                <div
                  v-if="section.description"
                  class="card-row"
                >
                  <div class="card-row__text">
                    <p>
                      {{ translate(section.description) }}
                    </p>
                  </div>
                </div>
                <template v-if="section.properties && section.properties.length > 0">
                  <div
                    v-for="(field, index) in section.properties"
                    v-show="dynamicForm.tree[currentPage][sectionIndex][field.field].exists()"
                    :key="index"
                    class="card-row"
                  >
                    <readonly-field-wrapper
                      :form-field="dynamicForm.tree[currentPage][sectionIndex][field.field]"
                    />
                  </div>
                </template>
              </section>
            </template>
            <empty-state
              v-else
              class="dynamic-form__empty-state"
            >
              <slot
                slot="title"
                name="empty-state-header"
              />
              <slot
                slot="description"
                name="empty-state-description"
              />
            </empty-state>
            <div class="btn-group dynamic-form__navigation">
              <button
                class="btn btn--light"
                :disabled="!hasPreviousStep && currentPage < 1"
                @click.prevent="previousStep"
              >
                {{ $t('actions.previous') }}
              </button>
              <button
                v-if="currentPage < pages.length - 1"
                class="btn btn--primary"
                @click.prevent="nextStep"
              >
                {{ $t('actions.next') }}
              </button>
              <button
                v-else
                type="submit"
                class="btn btn--primary"
                @click.prevent="nextStep"
              >
                <slot name="submit-action-text">
                  {{ $t('dynamic-form.actions.review') }}
                </slot>
              </button>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>
<script>
import TranslateApiStringMixin from '@/mixins/TranslateApiString'
import debounce from 'underscore/modules/debounce'
import { DynamicForm } from '@/components/dynamic-form/utils/form'
import { getSectionAnchor, getSectionLocation } from '@/components/dynamic-form/utils/navigation'
import { validationMixin } from 'vuelidate'
import WarningBox from '@/components/utils/WarningBox.vue'
import ReadonlyFieldWrapper from '@/components/dynamic-form/fields/inputs/helpers/ReadonlyFieldWrapper.vue'
import Vue from 'vue'

export default {
  components: {
    WarningBox,
    ReadonlyFieldWrapper,
    EmptyState: () => import('@/components/dynamic-form/EmptyState.vue')
  },
  mixins: [
    TranslateApiStringMixin,
    validationMixin
  ],
  provide () {
    return {
      FormFieldClass: this.dynamicForm.FormFieldClass
    }
  },
  props: {
    dynamicForm: {
      type: DynamicForm,
      required: true
    },
    hasPreviousStep: {
      type: Boolean,
      default: false
    }
  },
  validations () {
    return {
      form: this.dynamicForm.getValidations()
    }
  },
  data () {
    return {
      form: this.dynamicForm.form,
      errorsSnapshot: undefined
    }
  },
  computed: {
    ErrorFields () {
      return this.dynamicForm.FormFieldClass.ErrorFields
    },
    currentPage () {
      return this.$route.params.page ?? 0
    },
    currentSection () {
      return this.$route.params.section ?? (!!this.$slots['header-card-title'] && !!this.$slots['header-card-content'] ? -1 : 0)
    },
    pages () {
      return this.dynamicForm.structure.pages
    },
    sections () {
      return this.pages[this.currentPage].sections
    }
  },
  watch: {
    '$v.form.$anyDirty' (newValue) {
      this.$emit('touch', newValue)
    },
    '$v.form.$model': {
      deep: true,
      handler () {
        this.$emit('touch', this.$v.form.$anyDirty)
      }
    },
    '$v.form': {
      deep: false,
      handler (v) {
        this.dynamicForm.setVuelidate(v, false)
      }
    }
  },
  created () {
    this.dynamicForm.setVuelidate(this.$v.form, this.dynamicForm.$v === undefined)
    this.$nextTick(() => {
      this.$emit('touch', this.$v.form.$anyDirty)
    })
    // Register local components dynamically
    // We do this in the created() lifecycle hook because the components must be registered before mount
    const FormFieldClass = this.dynamicForm.FormFieldClass
    Object.assign(this.$options.components, FormFieldClass.ErrorComponents)

    // In this case, we had to register the form components globally because
    // 1) dynamically registered async imports caused the Composite component to unmount in the following example
    // e.g. DynamicForm -> ReadonlyFieldWrapper -> Array -> ReadonlyFieldWrapper -> Composite -> ReadonlyFieldWrapper -> Text
    // 2) cannot switch to dynamically registered sync import because of circular imports
    // (ReadonlyFieldWrapper imports Array, which imports ReadonlyFieldWrapper, ... if this is sync, it breaks at compile time)
    Object.entries(FormFieldClass.FormComponents).forEach(
      ([name, component]) => { Vue.component(name, component) }
    )
  },
  mounted () {
    if (this.$route.hash) {
      this.focusOnTargetSection()
    }
    window.addEventListener('scroll', this.onScroll)
  },
  async beforeDestroy () {
    if (this.errorsSnapshot) {
      await this.errorsSnapshot.destroy()
    }
    window.removeEventListener('scroll', this.onScroll)
  },
  methods: {
    onScroll: debounce(function () {
      if (window.document.activeElement?.id === this.getSectionAnchor(this.currentPage, this.currentSection)) {
        // we are focusing on a section already, don't navigate
        return
      }
      const sections = this.$refs.sections
      if (sections) {
        let current = sections.length - [...sections].reverse().findIndex(section => window.scrollY >= section.offsetTop - this.$route.meta.scrollOffset.y) - 1
        current = current >= sections.length ? 0 : current
        if (current !== this.currentSection) {
          this.$router.replace(this.getSectionLocation(this.currentPage, current, true))
        }
      }
    }, 100),
    getSectionAnchor (page, section) {
      return getSectionAnchor(page, section)
    },
    getSectionLocation (page, section, quiet = false) {
      return getSectionLocation(page, section, quiet)
    },
    previousStep () {
      if (this.currentPage < 1) {
        this.$emit('previous')
      } else {
        this.$router.replace(this.getSectionLocation(this.currentPage - 1, 0))
      }
    },
    async nextStep () {
      if (this.currentPage < this.pages.length - 1) {
        this.$router.replace(this.getSectionLocation(this.currentPage + 1, 0))
        return
      }
      this.$v.form.$touch()
      if (this.errorsSnapshot) {
        await this.errorsSnapshot.destroy()
      }
      if (this.$v.form.$invalid) {
        // we want errors to reflect the state of the form at the time that the user last tried to submit the form
        this.errorsSnapshot = this.dynamicForm.takeSnapshot()
        this.$nextTick(() => {
          const element = this.$refs['form-errors'].$el
          element.scrollIntoView() // bring element to top of screen
          window.scrollBy(0, -this.$route.meta.scrollOffset.y) // mind the top bar
        })
      } else {
        this.errorsSnapshot = undefined
        this.$emit('next')
      }
    },
    onPageChanged () {
      this.$nextTick(() => {
        this.focusOnTargetSection()
      })
    },
    focusOnTargetSection () {
      const sections = this.$refs.sections
      const targetSection = sections?.find(section => section.id === this.getSectionAnchor(this.currentPage, this.currentSection))
      if (targetSection) {
        this.$nextTick(() => {
          targetSection.scrollIntoView({ behavior: 'smooth' })
          targetSection.focus({ preventScroll: true })
        })
      }
    }
  }
}
</script>
<style lang="scss" scoped>
@import "@/assets/scss/variables";
.dynamic-form__sidebar {
  top: calc(2rem + #{$header-height});
  position: sticky;

  .dynamic-form__navigation {
    max-height: calc(100vh - 24rem);
    overflow-y: auto;

    p + ul {
      margin-top: 0;
    }

    .dynamic-form__navigation-page + .dynamic-form__navigation-page {
      margin-top: 1.5rem;
    }
  }
}

.dynamic-form__section {
  scroll-margin: #{$header-height + 2rem};
}

.dynamic-form__section:focus:not(:focus-visible) {
  /* Remove the focus indicator on mouse-focus for browsers
    that support :focus-visible */
  outline: none;
}

.dynamic-form__errors {
  margin-bottom: 2rem;
}

.dynamic-form__errors-description {
  margin-bottom: 1rem;
}

.dynamic-form__empty-state + .btn-group {
  margin-top: 1.5rem;
}

.dynamic-form__navigation {
  min-width: 16.5rem;
  .btn {
    min-width: 7.5rem;
  }
}
</style>
