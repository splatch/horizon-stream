<template>
  <div class="container">
    <PageHeader heading="Discovery" />
    <div>
      <Infobar />
    </div>
    <div class="discovery-cards">
      <Card
        :title="'Active Discovery'"
        :imgUrl="ActiveDiscoveryImg"
      >
        <template v-slot:footer>
          <div class="footer">
            <p>Select tool(s) to configure your nodes to send logs to BTO</p>
            <ActiveDiscoveryToolsList @show-config-active-tool="showConfigActiveTool" />
          </div>
        </template>
      </Card>
      <Card
        :title="'Passive Discovery'"
        :imgUrl="PassiveDiscoveryImg"
      >
        <template v-slot:footer>
          <div class="footer">
            <p>Select tool(s) to configure your nodes to send logs to BTO</p>
            <div class="passive-tools">
              <PassiveTool
                :label="'Syslog'"
                @show-settings="() => showSettings('Syslog')"
                @show-instructions="() => showInstructions('Syslog')"
              />
              <PassiveTool
                :label="'SNMP Traps'"
                @show-settings="() => showSettings('SNMP')"
                @show-instructions="() => showInstructions('SNMP')"
              />
            </div>
          </div>
        </template>
      </Card>
    </div>
  </div>
   <PrimaryModal :visible="isVisible" title="''" hide-title>
    <template #content>
      Configuration of {{selectedTool}}
    </template>
     <template v-slot:footer>
      <FeatherButton 
        secondary 
        @click="cancel">
          Cancel
      </FeatherButton>
    </template>
  </PrimaryModal>
   <FeatherDrawer
    id="map-left-drawer"
    :left="false"
    :modelValue="isDrawerOpen"
    @update:modelValue="closeDrawer"
    :labels="{ close: 'close', title: 'Instructions' }"
  >
    <div class="container">
      <slot name="search"><DiscoveryInstructions :tool="selectedTool"/></slot>
      <slot name="view"></slot>
    </div>
  </FeatherDrawer>
</template>

<script setup lang="ts">
import ActiveDiscoveryImg from '@/assets/active-discovery.png'
import PassiveDiscoveryImg from '@/assets/passive-discovery.png'
import useModal from '@/composables/useModal'
const { openModal, closeModal, isVisible } = useModal()
const selectedTool = ref('')
const isDrawerOpen = ref(false)

const showSettings = (tool: string) => {
  selectedTool.value = tool
  openModal()
}


const showInstructions = (tool: string) => {
  selectedTool.value = tool
  isDrawerOpen.value = true
}

const showConfigActiveTool = (tool: string) => {
  selectedTool.value = tool
  openModal()
}

const cancel = () => {
  closeModal()
}

const closeDrawer = () => {
  isDrawerOpen.value = false
}
</script>

<style scoped lang="scss">
@use '@featherds/styles/themes/variables';

.container {
	display: flex;
	flex-direction: column;
	margin: 0 1rem;

	.discovery-cards {
		display: flex;
	}

	.footer {
		padding: var(variables.$spacing-m);
		padding-top: var(variables.$spacing-s);

		> p {
			margin-bottom: 10px;
		}
	}

  .passive-tools {
    display: flex;
    flex-direction: column;
    margin-top: 18px;
  }
}
</style>
