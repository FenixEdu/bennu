<template>
  <modal
    v-model="isModalOpen"
    class="modal--md"
    :has-close-button="true"
    initial-focus="#modal-search__input"
    aria-labelledby="#modal-search__input"
  >
    <template #modal-panel>
      <form
        role="search"
        class="f-search"
      >
        <input
          id="modal-search__input"
          ref="searchInput"
          v-model="searchInput"
          :aria-label="$t('search.aria-label')"
          :placeholder="$t('search.placeholder')"
          class="f-search__input f-search__input--card"
          type="text"
          @keydown.enter.prevent="performSearch()"
        >
        <button
          class="f-search__submit"
          @click.prevent="performSearch()"
        >
          <span class="sr-only">
            {{ $t('search.aria-label') }}
          </span>
        </button>
      </form>
    </template>
  </modal>
</template>

<script>
import Modal from './utils/Modal.vue'

export default {
  components: {
    Modal
  },
  props: {
    value: {
      type: Boolean,
      required: true
    }
  },
  data () {
    return {
      searchInput: ''
    }
  },
  computed: {
    isModalOpen: {
      get () {
        return this.value
      },
      set (val) {
        this.$emit('input', val)
      }
    }
  },
  methods: {
    performSearch () {
      if (this.searchInput === this.$route.params.domainObjectId) {
        this.isModalOpen = false
        return
      }

      if (this.searchInput) {
        this.$router.push({
          name: 'DomainObjectPage',
          params: {
            domainObjectId: this.searchInput
          }
        })

        this.searchInput = ''
        this.isModalOpen = false
      }
    }
  },
  i18n: {
    messages: {
      pt: {
        search: {
          'aria-label': 'Pesquisar Objetos do Domínio...',
          placeholder: 'Pesquisar Objetos do Domínio...'
        }
      },
      en: {
        search: {
          'aria-label': 'Search Domain Objects...',
          placeholder: 'Search Domain Objects...'
        }
      }
    }
  }
}
</script>
