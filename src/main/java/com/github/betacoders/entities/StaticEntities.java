package com.github.betacoders.entities;

/**
 * StaticEntities
 * Entidades que permanecem estáticas ao longo da simulação.
 */
public sealed interface StaticEntities {
  /**
   * É a célula transitável padrão. Qualquer paciente pode caminhar livremente
   * por ela. O único impedimento é que duas entidades não podem ocupar a mesma
   * célula de chão ao mesmo tempo.
   */
  public record Floor() implements StaticEntities {
  }

  /**
   * Representa barreiras físicas intransponíveis. O algoritmo de busca de
   * caminhos deve ignorar estas células, e os personagens nunca podem
   * atravessá-las.
   */
  public record Wall() implements StaticEntities {
  }

  /**
   * Há exatamente um no mapa. É o ponto de entrada física na simulação. Novos
   * pacientes surgem nesta coordenada de forma lógica e visual, desde que a
   * célula esteja livre.
   * 
   */
  public record Generator() implements StaticEntities {
  }

  /**
   * Há exatamente um no mapa. É o ponto de saída. Quando um paciente que já
   * concluiu todo o seu tratamento médico pisa nesta célula, ele é retirado da
   * lista de agentes ativos e suas estatísticas são computadas pelo sistema.
   * 
   */
  public record Remover() implements StaticEntities {
  }

  /**
   * Dispositivo eletrônico onde o paciente retira sua senha de atendimento. O
   * paciente deve se deslocar até o totem logo após entrar no hospital. Apenas
   * um paciente pode interagir com cada totem por vez.
   */
  public record Totem() implements StaticEntities {
  }

  /**
   * Células onde os pacientes aguardam a sua vez de serem chamados (seja para
   * a triagem ou para o atendimento médico). Um assento possui estados lógicos
   * claros: livre, reservado (quando um paciente está caminhando em direção a
   * ele) e ocupado (quando o paciente está efetivamente sentado). Reservar o
   * assento antes de iniciar o deslocamento impede que dois pacientes caminhem
   * para a mesma cadeira.
   */
  public record Seat() implements StaticEntities {
  }

  /**
   * Posto fixo de atendimento de enfermagem. O paciente não deve pisar na
   * célula ocupada pela enfermeira. Ele deve se deslocar para uma célula de
   * chão livre adjacente (vizinha) à enfermeira. O atendimento é considerado
   * iniciado quando o paciente chega a essa posição adjacente.
   */
  public record Nurse() implements StaticEntities {
  }

  /**
   * Consultório médico de atendimento. Assim como na triagem, o paciente não
   * sobrepõe a célula do médico; ele se posiciona em uma célula livre
   * adjacente para realizar a consulta.
   */
  public record Medic() implements StaticEntities {
  }
}
