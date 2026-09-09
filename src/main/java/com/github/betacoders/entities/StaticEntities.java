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
  public final class Generator implements StaticEntities {
  }

  /**
   * Ha exatamente um no mapa. E o ponto de saida. Quando um paciente que ja
   * concluiu todo o seu tratamento medico pisa nesta celula, ele e retirado da
   * lista de agentes ativos e suas estatisticas sao computadas pelo sistema.
   * 
   */
  public final class Remover implements StaticEntities {
  }

  /**
   * Dispositivo eletronico onde o paciente retira sua senha de atendimento. O
   * paciente deve se deslocar ate o totem logo apos entrar no hospital. Apenas
   * um paciente pode interagir com cada totem por vez.
   */
  public final class Totem implements StaticEntities
  {
    private int lastNormalTicket = 0;
    private int lastPreferentialTicket = 0;

    public void issueTicket(Patient patient)
    {
      if (patient == null)
      {
        throw new IllegalArgumentException("The patient cannot be null.");
      }

      // O contador so avanca se o paciente receber a senha com sucesso.
      if (patient.preferential())
      {
        int number = lastPreferentialTicket + 1;
        patient.giveTicketNum(number);
        lastPreferentialTicket = number;
      }
      else
      {
        int number = lastNormalTicket + 1;
        patient.giveTicketNum(number);
        lastNormalTicket = number;
      }
    }
  }

  /**
   * Celulas onde os pacientes aguardam a sua vez de serem chamados (seja para
   * a triagem ou para o atendimento medico). Um assento possui estados logicos
   * claros: livre, reservado (quando um paciente esta caminhando em direcao a
   * ele) e ocupado (quando o paciente esta efetivamente sentado). Reservar o
   * assento antes de iniciar o deslocamento impede que dois pacientes caminhem
   * para a mesma cadeira.
   */
  public final class Seat implements StaticEntities {

    //3 estados possiveis do assento
    public enum State {
      FREE, RESERVED, OCCUPIED

    }

    private State state = State.FREE;  //assento comeca livre por padrao


    //metodo para marcar o assento como ocupado
    public void occupy() {
      state = State.OCCUPIED;
    }

    //metodo para marcar o assento como reservado
    public void reserve() {
      state = State.RESERVED;
    }

    //metodo para marcar o assento como liberado
    public void release() {
      state = State.FREE;
    }

    //consulta se o assento esta livre para ser reservado
    public boolean isFree() {
      return state == State.FREE; //compara o estado atual com LIVRE
    }

    //retorna o estado do assento
    public State getState() {
    return state;
  }

  }

  /**
   * Posto fixo de atendimento de enfermagem. O paciente nao deve pisar na
   * celula ocupada pela enfermeira. Ele deve se deslocar para uma celula de
   * chao livre adjacente (vizinha) a enfermeira. O atendimento e considerado
   * iniciado quando o paciente chega a essa posicao adjacente.
   */
  public final class Nurse implements StaticEntities {

    //2 possiveis estados da enfermaria
    public enum State {
      IDLE, OCCUPIED
    }

    private State state = State.IDLE; //enfermeira comeca ociosa por padrao

    //marca a enfermeira como ocupada (paciente comeca a ser atendido)
    public void occupy() {
      state = State.OCCUPIED;
    }

    //libera a enfermeira, voltando ao estado ocioso (fim do atendimento ou reset)
    public void release() {
      state = State.IDLE;
    }

    //retorna se a enfermeira esta livre para chamar o proximo paciente
    public boolean isIdle() {
      return state == State.IDLE;
    }

    //retorna o estado da enfermeira
    public State getState() {
      return state; 
    }
  }

  /**
   * Consultorio medico de atendimento. Assim como na triagem, o paciente nao
   * sobrepoe a celula do medico; ele se posiciona em uma celula livre
   * adjacente para realizar a consulta.
   */
  public final class Medic implements StaticEntities {

    //2 possiveis estados do medico
    public enum State {
      IDLE, OCCUPIED
    }

    private State state = State.IDLE; //medico comeca ocioso por padrao

    //marca o medico como ocupado (paciente comeca a ser atendido)
    public void occupy() {
      state = State.OCCUPIED;
    }

    //libera o medico, voltando ao estado ocioso (fim da consulta ou reset)
    public void release() {
      state = State.IDLE;
    }

    //retorna se o medico esta livre para chamar o proximo paciente
    public boolean isIdle() {
      return state == State.IDLE;
    }
    //retorna o estado do medico
    public State getState() {
      return state;
    }

  }

}
